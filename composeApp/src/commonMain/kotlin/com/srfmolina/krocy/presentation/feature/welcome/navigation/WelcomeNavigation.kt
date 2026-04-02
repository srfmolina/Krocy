package com.srfmolina.krocy.presentation.feature.welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.presentation.feature.welcome.WelcomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute

fun NavController.navigateToWelcome(
    navOptions: NavOptions? = null
) = navigate(route = WelcomeRoute, navOptions)

fun NavGraphBuilder.welcomeScreen() {
    composable<WelcomeRoute> {
        WelcomeScreen()
    }
}
