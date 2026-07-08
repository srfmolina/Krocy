package com.srfmolina.krocy.domain.model.product

/** A product-specific conversion to create: 1 [fromQuId] = [factor] [toQuId]. */
data class NewQuConversion(
    val productId: Int,
    val fromQuId: Int,
    val toQuId: Int,
    val factor: Double,
)
