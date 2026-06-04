package com.srfmolina.krocy.ui.presentation.feature.stock.model

import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi

internal data class StockItemUi(
    val id: Int,
    val name: String,
    val hints: List<String>,
    val consumptionDate: ConsumptionDateUi?,
    val quantity: String,
    val pictureUrl: String?
)