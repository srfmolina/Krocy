package com.srfmolina.krocy.ui.presentation.common.model

import androidx.compose.material3.ExperimentalMaterial3Api

internal enum class TopBarType {
    SMALL, MEDIUM
}

internal data class TopBarConfigurationUi @OptIn(ExperimentalMaterial3Api::class) constructor(
    val title: String,
    val type: TopBarType,
    val action: ActionUi? = null
)
