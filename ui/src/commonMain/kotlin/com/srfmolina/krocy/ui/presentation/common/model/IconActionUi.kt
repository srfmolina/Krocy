package com.srfmolina.krocy.ui.presentation.common.model

import androidx.compose.ui.graphics.vector.ImageVector

internal data class IconActionUi(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)