package com.srfmolina.krocy.domain.model.stock

import kotlinx.datetime.LocalDate

/**
 * A purchase to register. Amounts and price are already converted to the
 * product's stock quantity unit (the ViewModel converts from the unit the
 * user typed in, like Grocy's web form does).
 */
data class NewPurchase(
    val productId: Int,
    val amountStockUnits: Double,
    val dueDate: LocalDate?,
    val pricePerStockUnit: Double? = null,
    val locationId: Int? = null,
    val shoppingLocationId: Int? = null,
    val note: String? = null,
)
