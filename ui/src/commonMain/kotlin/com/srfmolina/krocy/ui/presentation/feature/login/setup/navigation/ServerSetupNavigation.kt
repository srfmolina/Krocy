package com.srfmolina.krocy.ui.presentation.feature.login.setup.navigation

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.navigation.ServerSetupRoute

internal fun NavController.navigateToServerSetup(
    navOptions: NavOptions? = null
) = navigate(route = ServerSetupRoute, navOptions)

internal fun NavGraphBuilder.serverSetupScreen(
    navController: NavController
) {
    composable<ServerSetupRoute> {
        Text("Configuración del servidor") // Replaced by ServerSetupScreen in a later task
    }
}
