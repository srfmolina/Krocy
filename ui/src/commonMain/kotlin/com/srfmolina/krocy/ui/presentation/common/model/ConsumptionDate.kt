package com.srfmolina.krocy.ui.presentation.common.model

import kotlinx.datetime.LocalDateTime
enum class ConsumptionType {
    EXPIRATION, PREFERENCE
}

data class ConsumptionDate(
    val type: ConsumptionType,
    val date: LocalDateTime
)
