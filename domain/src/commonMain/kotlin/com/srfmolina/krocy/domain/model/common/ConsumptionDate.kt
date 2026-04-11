package com.srfmolina.krocy.domain.model.common

import kotlinx.datetime.LocalDateTime

data class ConsumptionDate(
    val type: ConsumptionType,
    val date: LocalDateTime,
    val expired: Boolean
)