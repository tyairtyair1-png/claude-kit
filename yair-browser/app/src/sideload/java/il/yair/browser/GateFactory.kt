package il.yair.browser

import android.app.Activity

object GateFactory {
    fun create(a: Activity): Gate = object : Gate {
        override fun start(onChange: (Boolean) -> Unit) = onChange(true)
        override fun refresh() {}
        override fun buy() {}
        override fun manage() {}
        override val priceText: String? = null
        override fun stop() {}
    }
}
