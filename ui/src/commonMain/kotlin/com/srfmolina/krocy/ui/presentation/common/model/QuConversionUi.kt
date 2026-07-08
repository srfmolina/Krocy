package com.srfmolina.krocy.ui.presentation.common.model

/** A global quantity-unit conversion as needed by selection UIs: 1 from = factor to. */
internal data class QuConversionUi(
    val fromQuId: Int,
    val toQuId: Int,
    val factor: Double,
)
