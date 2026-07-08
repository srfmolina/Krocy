package com.srfmolina.krocy.domain.model.masterdata

/** A quantity-unit conversion; [productId] is null for global conversions. */
data class QuConversion(
    val id: Int,
    val productId: Int?,
    val fromQuId: Int,
    val toQuId: Int,
    val factor: Double,
)
