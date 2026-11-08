package il.yair.browser

import java.net.URI

/** A rule: [host] itself, plus its subdomains when [subdomains] is true. */
data class Rule(val host: String, val subdomains: Boolean)

enum class Decision { ALLOW, BLOCK_SCHEME, BLOCK_HOST, HARD_DENY }

/** Pure allowlist logic (no Android dependencies, unit tested). */
class Policy(
    private val base: List<Rule>,
    private val extra: List<Rule>,
    private val removedBase: Set<String>,
) {
    fun decide(url: String): Decision {
        val uri = try { URI(url) } catch (e: Exception) { return Decision.BLOCK_SCHEME }
        if (uri.scheme?.lowercase() != "https") return Decision.BLOCK_SCHEME
        if (uri.rawUserInfo != null) return Decision.BLOCK_HOST
        if (uri.port != -1 && uri.port != 443) return Decision.BLOCK_HOST
        val host = normalize(uri.host ?: return Decision.BLOCK_HOST)
        return decideHost(host)
    }

    fun decideHost(host: String): Decision {
        if (isIp(host) || !host.contains('.')) return Decision.BLOCK_HOST
        if (HARD_DENY.any { matches(host, it, true) }) return Decision.HARD_DENY
        val ok = base.any { it.host !in removedBase && matches(host, it.host, it.subdomains) } ||
            extra.any { matches(host, it.host, it.subdomains) }
        return if (ok) Decision.ALLOW else Decision.BLOCK_HOST
    }

    companion object {
        /** Categories the owner asked never to open (messaging, social, shopping). Cannot be approved. */
        val HARD_DENY = listOf(
            "whatsapp.com", "whatsapp.net", "wa.me", "telegram.org", "telegram.me", "t.me",
            "facebook.com", "fb.com", "fbcdn.net", "messenger.com", "instagram.com", "twitter.com",
            "x.com", "tiktok.com", "snapchat.com", "linkedin.com", "pinterest.com", "reddit.com",
            "discord.com", "signal.org", "amazon.com", "aliexpress.com", "ebay.com", "shein.com",
            "temu.com", "ksp.co.il", "ivory.co.il", "zap.co.il", "wolt.com", "10bis.co.il",
        )

        fun normalize(h: String) = h.trim().lowercase().trimEnd('.')

        fun matches(host: String, domain: String, subdomains: Boolean) =
            host == domain || (subdomains && host.endsWith(".$domain"))

        private fun isIp(h: String) =
            h.contains(':') || h.matches(Regex("""\d{1,3}(\.\d{1,3}){3}"""))
    }
}
