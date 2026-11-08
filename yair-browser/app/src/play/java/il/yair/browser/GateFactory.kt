package il.yair.browser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.android.billingclient.api.*

object GateFactory {
    fun create(a: Activity): Gate = PlayGate(a)
}

/**
 * Google Play Billing subscription gate. Product id "yair_monthly" (base plan: monthly) must exist in Play Console.
 * Entitlement is cached for OFFLINE_GRACE_MS after the last successful Play check so the app works offline.
 * Client-side check only (no backend); acceptable for a low-value consumer subscription.
 */
private class PlayGate(private val act: Activity) : Gate {
    companion object {
        const val PRODUCT_ID = "yair_monthly"
        const val OFFLINE_GRACE_MS = 3L * 24 * 3600 * 1000
    }
    private val prefs = act.getSharedPreferences("entitlement", Activity.MODE_PRIVATE)
    private var cb: ((Boolean) -> Unit)? = null
    private var details: ProductDetails? = null
    override var priceText: String? = null
    private var client: BillingClient? = null

    private val listener = PurchasesUpdatedListener { r, list ->
        if (r.responseCode == BillingClient.BillingResponseCode.OK && list != null) handle(list)
    }

    override fun start(onChange: (Boolean) -> Unit) {
        cb = onChange
        onChange(cachedActive())
        client = BillingClient.newBuilder(act).setListener(listener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .enableAutoServiceReconnection().build()
        client!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(r: BillingResult) { if (r.responseCode == BillingClient.BillingResponseCode.OK) { loadDetails(); refresh() } }
            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun loadDetails() {
        val q = QueryProductDetailsParams.newBuilder().setProductList(listOf(
            QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_ID).setProductType(BillingClient.ProductType.SUBS).build())).build()
        client?.queryProductDetailsAsync(q) { _, res ->
            details = res.productDetailsList.firstOrNull()
            val phase = details?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.lastOrNull()
            priceText = phase?.let { "${it.formattedPrice} לחודש" }
            act.runOnUiThread { cb?.invoke(cachedActive()) }
        }
    }

    override fun refresh() {
        val c = client ?: return
        if (!c.isReady) return
        c.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()) { r, list ->
            if (r.responseCode == BillingClient.BillingResponseCode.OK) handle(list)
        }
    }

    private fun handle(list: List<Purchase>) {
        val active = list.filter { it.products.contains(PRODUCT_ID) && it.purchaseState == Purchase.PurchaseState.PURCHASED }
        active.filter { !it.isAcknowledged }.forEach {
            client?.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(it.purchaseToken).build()) {}
        }
        // Successful query: record the result (also clears entitlement when the subscription lapsed).
        prefs.edit().putBoolean("active", active.isNotEmpty()).putLong("checkedAt", System.currentTimeMillis()).apply()
        act.runOnUiThread { cb?.invoke(active.isNotEmpty()) }
    }

    private fun cachedActive() = prefs.getBoolean("active", false) &&
        System.currentTimeMillis() - prefs.getLong("checkedAt", 0) < OFFLINE_GRACE_MS

    override fun buy() {
        val d = details ?: return
        val offer = d.subscriptionOfferDetails?.firstOrNull() ?: return
        val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(d).setOfferToken(offer.offerToken).build())).build()
        client?.launchBillingFlow(act, params)
    }

    override fun manage() {
        act.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?sku=$PRODUCT_ID&package=${act.packageName}")))
    }

    override fun stop() { client?.endConnection(); client = null }
}
