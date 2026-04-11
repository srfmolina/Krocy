package com.srfmolina.krocy.ui.presentation.common.model

import com.srfmolina.krocy.domain.model.common.ConsumptionType

internal data class ConsumptionDateUi(
    val type: ConsumptionType,
    val date: String,
    val expired: Boolean
)
