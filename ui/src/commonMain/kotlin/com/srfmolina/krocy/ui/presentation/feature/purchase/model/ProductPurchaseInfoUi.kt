package com.srfmolina.krocy.ui.presentation.feature.purchase.model

/** Unit/price info the purchase form keeps after a product is selected. */
internal data class ProductPurchaseInfoUi(
    val purchaseUnitName: String,
    val purchaseUnitNamePlural: String,
    val stockUnitName: String,
    val stockUnitNamePlural: String,
    val conversionFactorPurchaseToStock: Double,
    val lastPrice: Double?,
)
