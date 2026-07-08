package com.srfmolina.krocy.domain.model.product

/**
 * Everything the purchase form needs to prefill itself for a product.
 *
 * [defaultBestBeforeDays] follows Grocy semantics: -1 never expires,
 * 0 no default, >0 days from today. [lastPrice] is per stock unit.
 */
data class ProductPurchaseInfo(
    val productId: Int,
    val purchaseUnitName: String,
    val purchaseUnitNamePlural: String,
    val stockUnitName: String,
    val stockUnitNamePlural: String,
    val conversionFactorPurchaseToStock: Double,
    val defaultBestBeforeDays: Int?,
    val defaultLocationId: Int?,
    val lastPrice: Double?,
    val lastShoppingLocationId: Int?,
)
