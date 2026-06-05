package com.srfmolina.krocy.ui.presentation.feature.creation.mapper

import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi

internal fun QuantityUnit.toOption(): SelectableOptionUi = SelectableOptionUi(id = id, label = name)

internal fun Location.toOption(): SelectableOptionUi = SelectableOptionUi(id = id, label = name)

internal fun ProductGroup.toOption(): SelectableOptionUi = SelectableOptionUi(id = id, label = name)
