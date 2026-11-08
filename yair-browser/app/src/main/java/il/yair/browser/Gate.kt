package il.yair.browser

import android.app.Activity

/** Access gate. Sideload build: always open. Play build: open only with an active monthly subscription. */
interface Gate {
    fun start(onChange: (open: Boolean) -> Unit)
    fun refresh()
    fun buy()
    fun manage()
    /** Localized price line from Google Play (e.g. "₪19.90 לחודש"), null until loaded. */
    val priceText: String?
    fun stop()
}
