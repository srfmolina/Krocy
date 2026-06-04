package com.srfmolina.krocy.ui.presentation.common.model

import androidx.compose.ui.graphics.vector.ImageVector

internal data class LabeledActionUi(
    val label: String,
    val contentDescription: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit
)
