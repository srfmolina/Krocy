package com.srfmolina.krocy.ui.presentation.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.login.LoginScreen
import com.srfmolina.krocy.ui.presentation.feature.login.setup.navigation.navigateToServerSetup
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.navigateToStock
import com.srfmolina.krocy.ui.presentation.navigation.LoginRoute


internal fun NavController.navigateToLogin(
    navOptions: NavOptions? = null
) = navigate(route = LoginRoute, navOptions)

internal fun NavGraphBuilder.loginScreen(
    navController: NavController,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit
) {
    composable<LoginRoute> {
        LoginScreen(
            onNavigateToStock = {
                navController.navigateToStock(
                    navOptions = navOptions { popUpTo(0) { inclusive = true } }
                )
            },
            onNavigateToServerSetup = navController::navigateToServerSetup,
            onShowSnackbar = onShowSnackbar
        )
    }
}
