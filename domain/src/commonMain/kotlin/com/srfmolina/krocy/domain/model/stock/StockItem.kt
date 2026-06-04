package com.srfmolina.krocy.domain.model.stock

import com.srfmolina.krocy.domain.model.common.ConsumptionDate

data class StockItem(
    val id: Int,
    val name: String,
    val hints: List<String>,
    val consumptionDate: ConsumptionDate?,
    val quantity: String,
    val pictureUrl: String?
)