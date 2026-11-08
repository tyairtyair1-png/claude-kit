package il.yair.browser

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** Target SDK 35+ is edge-to-edge: keep content clear of status/navigation bars and the keyboard. */
fun View.applySystemInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, w ->
        val i = w.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
        v.setPadding(i.left, i.top, i.right, i.bottom)
        WindowInsetsCompat.CONSUMED
    }
}
