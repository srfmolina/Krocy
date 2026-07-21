package com.srfmolina.krocy.ui.presentation.feature.login.setup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupScreen
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.navigateToStock
import com.srfmolina.krocy.ui.presentation.navigation.ServerSetupRoute

internal fun NavController.navigateToServerSetup(
    navOptions: NavOptions? = null
) = navigate(route = ServerSetupRoute, navOptions)

internal fun NavGraphBuilder.serverSetupScreen(
    navController: NavController
) {
    composable<ServerSetupRoute> {
        ServerSetupScreen(
            onNavigateToStock = {
                navController.navigateToStock(
                    navOptions = navOptions { popUpTo(0) { inclusive = true } }
                )
            },
            onNavigateBack = navController::navigateUp
        )
    }
}
