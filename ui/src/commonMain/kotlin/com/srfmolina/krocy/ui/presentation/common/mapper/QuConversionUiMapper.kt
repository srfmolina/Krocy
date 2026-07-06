package com.srfmolina.krocy.ui.presentation.common.mapper

import com.srfmolina.krocy.domain.model.masterdata.QuConversion
import com.srfmolina.krocy.ui.presentation.common.model.QuConversionUi

internal fun QuConversion.toUi(): QuConversionUi =
    QuConversionUi(fromQuId = fromQuId, toQuId = toQuId, factor = factor)
