package com.srfmolina.krocy.domain.model.product

data class NewProduct(
    val name: String,
    val quIdStock: Int,
    val quIdPurchase: Int,
    val locationId: Int,
    val description: String? = null,
    val minStockAmount: Double? = null,
    val productGroupId: Int? = null,
)
