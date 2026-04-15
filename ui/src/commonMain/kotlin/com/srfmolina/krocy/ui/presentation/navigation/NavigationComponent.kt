package com.srfmolina.krocy.ui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi

@Composable
internal fun NavigationComponent(
    navController: NavHostController,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit
) {
    NavHost(navController = navController, startDestination = AppRoute) {
        appNavGraph(navController, onChangeTopBar)
    }
}
