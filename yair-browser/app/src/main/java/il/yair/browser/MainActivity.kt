package il.yair.browser

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.*
import androidx.activity.ComponentActivity
import java.io.ByteArrayInputStream
import java.net.URI

class MainActivity : ComponentActivity() {
    private lateinit var store: RulesStore
    private lateinit var web: WebView
    private lateinit var home: ScrollView
    private lateinit var urlBox: EditText
    private lateinit var progress: ProgressBar
    private lateinit var msg: TextView
    @Volatile private var topSite: String? = null

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        store = RulesStore(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutDirection = View.LAYOUT_DIRECTION_RTL }
        val bar = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(8, 8, 8, 8) }
        fun btn(t: String, a: () -> Unit) = Button(this).apply { text = t; minWidth = 0; setOnClickListener { a() } }
        urlBox = EditText(this).apply {
            hint = "כתובת אתר"; setSingleLine(); inputType = InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_GO; layoutDirection = View.LAYOUT_DIRECTION_LTR
            setOnEditorActionListener { _, _, _ -> go(text.toString()); true }
        }
        bar.addView(btn("⌂") { showHome() })
        bar.addView(btn("→") { if (web.canGoBack()) web.goBack() else showHome() })
        bar.addView(urlBox, LinearLayout.LayoutParams(0, -2, 1f))
        bar.addView(btn("⋮") { openAdmin() })
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100; visibility = View.GONE }
        msg = TextView(this).apply { setPadding(24, 12, 24, 12); visibility = View.GONE; setBackgroundColor(0xFFFFF3CD.toInt()) }
        web = WebView(this)
        home = ScrollView(this)
        root.addView(bar); root.addView(progress, LinearLayout.LayoutParams(-1, 8)); root.addView(msg)
        root.addView(home, LinearLayout.LayoutParams(-1, 0, 1f)); root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        setupWeb()
        buildHome()
        showHome()
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { if (web.visibility == View.VISIBLE && web.canGoBack()) web.goBack() else if (web.visibility == View.VISIBLE) showHome() else finish() }
        })
    }

    override fun onResume() { super.onResume(); buildHome() }

    private fun setupWeb() {
        web.settings.apply {
            javaScriptEnabled = true; domStorageEnabled = true
            allowFileAccess = false; allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false); setGeolocationEnabled(false)
            safeBrowsingEnabled = true
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true)
        web.setDownloadListener { _, _, _, _, _ -> toast("הורדת קבצים אינה מופעלת") }
        // Service workers fetch outside the page client: apply the same policy.
        ServiceWorkerController.getInstance().setServiceWorkerClient(object : ServiceWorkerClient() {
            override fun shouldInterceptRequest(r: WebResourceRequest): WebResourceResponse? = check(r.url.toString(), false)
        })
        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(v: WebView, p: Int) { progress.progress = p; progress.visibility = if (p < 100) View.VISIBLE else View.GONE }
        }
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean {
                val u = r.url.toString()
                val d = store.policy().decide(u)
                if (d == Decision.ALLOW) return false
                if (r.isForMainFrame) blockedNav(u, d)
                return true
            }
            override fun shouldInterceptRequest(v: WebView, r: WebResourceRequest): WebResourceResponse? = check(r.url.toString(), r.isForMainFrame)
            override fun onPageStarted(v: WebView, url: String, f: Bitmap?) {
                hostOf(url)?.let { topSite = it }
                urlBox.setText(url)
            }
            override fun onReceivedSslError(v: WebView, h: SslErrorHandler, e: android.net.http.SslError) { h.cancel() }
        }
    }

    /** Returns an empty 403 for blocked requests; records blocked hosts for the owner's review. */
    private fun check(url: String, main: Boolean): WebResourceResponse? {
        val d = store.policy().decide(url)
        if (d == Decision.ALLOW) return null
        val h = hostOf(url)
        if (h != null && d != Decision.HARD_DENY && d != Decision.BLOCK_SCHEME) store.recordBlocked(h, if (main) null else topSite)
        return WebResourceResponse("text/plain", "utf-8", 403, "Blocked", emptyMap(), ByteArrayInputStream(ByteArray(0)))
    }

    private fun blockedNav(url: String, d: Decision) {
        val h = hostOf(url)
        runOnUiThread {
            when {
                d == Decision.HARD_DENY -> note("האתר $h חסום (תקשורת / רשתות חברתיות / קניות).")
                d == Decision.BLOCK_SCHEME -> note("רק כתובות HTTPS מותרות.")
                else -> { if (h != null) store.recordBlocked(h, topSite); note("האתר $h אינו ברשימה. נרשם לאישור בניהול (⋮).") }
            }
        }
    }

    private fun go(input: String) {
        val t = input.trim()
        if (t.isEmpty()) return
        val url = if (t.startsWith("http", true)) t else "https://$t"
        val d = store.policy().decide(url)
        if (d != Decision.ALLOW) { blockedNav(url, d); return }
        msg.visibility = View.GONE; home.visibility = View.GONE; web.visibility = View.VISIBLE
        web.loadUrl(url)
    }

    private fun showHome() { web.visibility = View.GONE; home.visibility = View.VISIBLE; msg.visibility = View.GONE; urlBox.setText("") }

    private fun buildHome() {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 16, 24, 32) }
        val pol = store.policy()
        fun add(c: String, hosts: List<String>) {
            col.addView(TextView(this).apply { text = c; textSize = 18f; setPadding(0, 24, 0, 8) })
            hosts.filter { pol.decideHost(it) == Decision.ALLOW }.forEach { h ->
                col.addView(Button(this).apply {
                    text = h; isAllCaps = false; layoutDirection = View.LAYOUT_DIRECTION_LTR
                    setOnClickListener { go(h) }
                })
            }
        }
        store.categories.forEach { add(it.name, it.domains) }
        val extras = store.extraRules().map { it.host }
        if (extras.isNotEmpty()) add("נוספו על ידי", extras)
        home.removeAllViews(); home.addView(col)
    }

    private fun note(t: String) { msg.text = t; msg.visibility = View.VISIBLE }
    private fun toast(t: String) = Toast.makeText(this, t, Toast.LENGTH_SHORT).show()
    private fun hostOf(u: String) = try { URI(u).host?.let(Policy::normalize) } catch (e: Exception) { null }

    private fun openAdmin() {
        val box = EditText(this).apply { inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD }
        val first = !store.hasPin()
        AlertDialog.Builder(this).setTitle(if (first) "קביעת קוד ניהול (4 ספרות ומעלה)" else "קוד ניהול").setView(box)
            .setPositiveButton("אישור") { _, _ ->
                val p = box.text.toString()
                if (first) { if (p.length >= 4) { store.setPin(p); startActivity(Intent(this, AdminActivity::class.java)) } else toast("הקוד קצר מדי") }
                else if (store.checkPin(p)) startActivity(Intent(this, AdminActivity::class.java)) else toast("קוד שגוי")
            }.setNegativeButton("ביטול", null).show()
    }
}
