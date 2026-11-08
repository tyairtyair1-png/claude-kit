package il.yair.browser

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.*
import androidx.activity.ComponentActivity
import java.io.File

/** Shows a downloaded PDF inside the app (no external viewer). */
class PdfActivity : ComponentActivity() {
    private var renderer: PdfRenderer? = null
    private var pfd: ParcelFileDescriptor? = null

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val file = File(intent.getStringExtra("path") ?: return finish())
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.DKGRAY) }
        setContentView(ScrollView(this).apply { addView(col); applySystemInsets() })
        try {
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val r = PdfRenderer(pfd!!).also { renderer = it }
            val w = resources.displayMetrics.widthPixels
            for (i in 0 until r.pageCount) {
                r.openPage(i).use { p ->
                    val bmp = Bitmap.createBitmap(w, (w * p.height.toFloat() / p.width).toInt(), Bitmap.Config.ARGB_8888)
                    bmp.eraseColor(Color.WHITE)
                    p.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    col.addView(ImageView(this).apply { setImageBitmap(bmp); setPadding(0, 0, 0, 8) })
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "לא ניתן לפתוח את הקובץ", Toast.LENGTH_LONG).show(); finish()
        }
    }

    override fun onDestroy() { renderer?.close(); pfd?.close(); super.onDestroy() }
}
