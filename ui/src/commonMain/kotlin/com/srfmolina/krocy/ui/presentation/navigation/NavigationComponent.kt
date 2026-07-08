package com.srfmolina.krocy.ui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi

@Composable
internal fun NavigationComponent(
    navController: NavHostController,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onOpenNavRail: () -> Unit,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit
) {
    NavHost(navController = navController, startDestination = AppRoute) {
        appNavGraph(navController, onChangeTopBar, onChangeFab, onOpenNavRail, onShowSnackbar)
    }
}
