package com.srfmolina.krocy.ui.presentation.common.model
enum class ConsumptionType {
    EXPIRATION, PREFERENCE
}

data class ConsumptionDate(
    val type: ConsumptionType,
    val date: String,
    val expired: Boolean
)
