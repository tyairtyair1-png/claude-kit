package il.yair.browser

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom

/** Persistent state: base list (assets), owner additions/removals, discovered (pending) domains, PIN. */
class RulesStore(private val ctx: Context) {
    private val prefs = ctx.getSharedPreferences("yair", Context.MODE_PRIVATE)

    data class Category(val name: String, val domains: List<String>)
    data class Pending(val host: String, val sites: Set<String>, val count: Int)

    val categories: List<Category> by lazy {
        val j = JSONObject(ctx.assets.open("whitelist.json").bufferedReader().readText())
        val a = j.getJSONArray("categories")
        (0 until a.length()).map {
            val c = a.getJSONObject(it)
            Category(c.getString("name"), c.getJSONArray("domains").let { d -> (0 until d.length()).map(d::getString) })
        }
    }

    /** Exact-host support domains (login, CAPTCHA, fonts, CDN) approved by the owner on 2026-11-08. */
    val infra: List<String> by lazy {
        val a = JSONObject(ctx.assets.open("whitelist.json").bufferedReader().readText()).getJSONArray("infra")
        (0 until a.length()).map(a::getString)
    }
    fun baseRules() = categories.flatMap { it.domains }.distinct().map { Rule(it, true) } +
        infra.filter { h -> categories.none { h in it.domains } }.map { Rule(it, false) }
    fun removedBase(): Set<String> = strSet("removed")
    /** extra entries: "host" = host+subdomains (added manually), "=host" = exact host only (approved third party). */
    fun extraRules() = strSet("extra").map { if (it.startsWith("=")) Rule(it.drop(1), false) else Rule(it, true) }
    fun extraRaw(): Set<String> = strSet("extra")

    @Synchronized fun policy() = Policy(baseRules(), extraRules(), removedBase())

    @Synchronized fun addManual(domain: String): Boolean {
        val d = Policy.normalize(domain.removePrefix("https://").substringBefore('/'))
        if (!d.matches(Regex("[a-z0-9-]+(\\.[a-z0-9-]+)+"))) return false
        if (Policy.HARD_DENY.any { Policy.matches(d, it, true) }) return false
        putSet("removed", removedBase() - d)
        putSet("extra", extraRaw() + d)
        clearPending(d)
        return true
    }

    @Synchronized fun approveExact(host: String): Boolean {
        if (Policy.HARD_DENY.any { Policy.matches(host, it, true) }) return false
        putSet("extra", extraRaw() + "=$host")
        clearPending(host)
        return true
    }

    @Synchronized fun remove(entry: String) {
        if (entry in extraRaw()) putSet("extra", extraRaw() - entry)
        else putSet("removed", removedBase() + entry)
    }

    // ---- discovery ----
    @Synchronized fun recordBlocked(host: String, site: String?) {
        val arr = JSONObject(prefs.getString("pending", "{}")!!)
        val o = arr.optJSONObject(host) ?: JSONObject().put("n", 0).put("s", JSONArray())
        o.put("n", o.getInt("n") + 1)
        val sites = o.getJSONArray("s")
        if (site != null && (0 until sites.length()).none { sites.getString(it) == site } && sites.length() < 20) sites.put(site)
        arr.put(host, o)
        prefs.edit().putString("pending", arr.toString()).apply()
    }

    @Synchronized fun pending(): List<Pending> {
        val arr = JSONObject(prefs.getString("pending", "{}")!!)
        return arr.keys().asSequence().map { h ->
            val o = arr.getJSONObject(h)
            Pending(h, o.getJSONArray("s").let { s -> (0 until s.length()).map(s::getString).toSet() }, o.getInt("n"))
        }.sortedByDescending { it.count }.toList()
    }

    @Synchronized fun clearPending(host: String) {
        val arr = JSONObject(prefs.getString("pending", "{}")!!)
        arr.remove(host)
        prefs.edit().putString("pending", arr.toString()).apply()
    }

    // ---- PIN ----
    fun hasPin() = prefs.contains("pinHash")
    fun setPin(pin: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }
        prefs.edit().putString("pinSalt", salt).putString("pinHash", hash(salt, pin)).apply()
    }
    fun checkPin(pin: String) = hash(prefs.getString("pinSalt", "")!!, pin) == prefs.getString("pinHash", null)
    private fun hash(salt: String, pin: String) =
        MessageDigest.getInstance("SHA-256").digest((salt + pin).toByteArray()).joinToString("") { "%02x".format(it) }

    private fun strSet(k: String): Set<String> = prefs.getStringSet(k, emptySet())!!.toSet()
    private fun putSet(k: String, v: Set<String>) = prefs.edit().putStringSet(k, v).apply()
}
