package com.srfmolina.krocy.presentation.feature.splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.presentation.feature.splash.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavController.navigateToSplash(
    navOptions: NavOptions? = null
) = navigate(route = SplashRoute, navOptions)

fun NavGraphBuilder.splashScreen() {
    composable<SplashRoute> {
        SplashScreen()
    }
}