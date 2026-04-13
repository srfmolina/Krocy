package com.srfmolina.krocy.ui.presentation.feature.welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.feature.login.navigation.navigateToLogin
import com.srfmolina.krocy.ui.presentation.feature.welcome.WelcomeScreen
import com.srfmolina.krocy.ui.presentation.navigation.WelcomeRoute


fun NavController.navigateToWelcome(
    navOptions: NavOptions? = null
) = navigate(route = WelcomeRoute, navOptions)

fun NavGraphBuilder.welcomeScreen(
    navController: NavController
) {
    composable<WelcomeRoute> {
        WelcomeScreen(onStart = navController::navigateToLogin)
    }
}
