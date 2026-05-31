package com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model

import androidx.compose.material3.ExperimentalMaterial3Api
import com.srfmolina.krocy.ui.presentation.common.model.ActionUi

internal data class TopBarConfigurationUi @OptIn(ExperimentalMaterial3Api::class) constructor(
    val title: String,
    val type: TopBarTypeUi,
    val leadingAction: ActionUi,
    val trailingAction: ActionUi? = null
)
