package il.yair.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity

/** PIN-gated: review discovered domains, approve/dismiss individually, add/remove domains, export report. */
class AdminActivity : ComponentActivity() {
    private lateinit var store: RulesStore
    private lateinit var col: LinearLayout

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        store = RulesStore(this)
        col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 24, 24, 48); layoutDirection = View.LAYOUT_DIRECTION_RTL }
        setContentView(ScrollView(this).apply { addView(col) })
        render()
    }

    private fun render() {
        col.removeAllViews()
        fun title(t: String) = col.addView(TextView(this).apply { text = t; textSize = 18f; setPadding(0, 28, 0, 8) })
        fun ltr(t: String) = TextView(this).apply { text = t; layoutDirection = View.LAYOUT_DIRECTION_LTR; textSize = 15f }

        title("הוספת דומיין (כולל תתי-דומיינים)")
        val input = EditText(this).apply { hint = "example.co.il"; inputType = InputType.TYPE_TEXT_VARIATION_URI; layoutDirection = View.LAYOUT_DIRECTION_LTR }
        col.addView(input)
        col.addView(Button(this).apply { text = "הוסף"; setOnClickListener {
            if (store.addManual(input.text.toString())) render() else Toast.makeText(context, "דומיין לא תקין או חסום קבוע", Toast.LENGTH_SHORT).show()
        } })

        val pend = store.pending()
        title("ממתינים לאישור (${pend.size})")
        if (pend.isNotEmpty()) col.addView(Button(this).apply { text = "העתק דוח לאישור"; setOnClickListener {
            val rep = pend.joinToString("\n") { "${it.host}  (x${it.count})  מופיע ב: ${it.sites.joinToString(", ")}" }
            (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("pending", rep))
            Toast.makeText(context, "הועתק", Toast.LENGTH_SHORT).show()
        } })
        pend.forEach { p ->
            val hard = Policy.HARD_DENY.any { Policy.matches(p.host, it, true) }
            col.addView(ltr("${p.host}  (x${p.count})"))
            col.addView(TextView(this).apply { text = "נדרש באתרים: " + p.sites.joinToString(", ") })
            val row = LinearLayout(this)
            if (!hard) row.addView(Button(this).apply { text = "אשר דומיין זה בלבד"; setOnClickListener { store.approveExact(p.host); render() } })
            row.addView(Button(this).apply { text = "התעלם"; setOnClickListener { store.clearPending(p.host); render() } })
            col.addView(row)
        }

        title("מותרים כרגע")
        val removed = store.removedBase()
        store.categories.flatMap { it.domains }.distinct().filter { it !in removed }.forEach { d -> entry(d, d) }
        title("דומיינים נלווים (התחברות, CAPTCHA, פונטים) — מארח מדויק")
        store.infra.filter { it !in removed }.forEach { d -> entry(d, d) }
        store.extraRaw().sorted().forEach { e -> entry(e.removePrefix("="), e) }
        if (removed.isNotEmpty()) {
            title("הוסרו מהרשימה")
            removed.sorted().forEach { d -> col.addView(ltr(d)); col.addView(Button(this).apply { text = "החזר"; setOnClickListener { store.addManual(d); store.remove(d); render() } }) }
        }
    }

    private fun entry(label: String, key: String) {
        val row = LinearLayout(this).apply { gravity = android.view.Gravity.CENTER_VERTICAL }
        row.addView(TextView(this).apply { text = label; layoutDirection = View.LAYOUT_DIRECTION_LTR }, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(Button(this).apply { text = "הסר"; setOnClickListener { store.remove(key); render() } })
        col.addView(row)
    }
}
