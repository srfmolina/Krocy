package com.srfmolina.krocy.domain.model.common

import java.time.LocalDateTime

data class ConsumptionDate(
    val type: ConsumptionType,
    val date: LocalDateTime,
    val expired: Boolean
)