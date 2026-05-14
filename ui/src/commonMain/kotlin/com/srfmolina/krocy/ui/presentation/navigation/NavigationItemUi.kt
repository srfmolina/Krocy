package com.srfmolina.krocy.ui.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

internal data class NavigationItemUi(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String = label,
    val navigateTo: () -> Unit,
    val route: KrocyRoute
)