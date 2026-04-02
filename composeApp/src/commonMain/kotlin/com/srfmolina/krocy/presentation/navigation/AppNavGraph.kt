package com.srfmolina.krocy.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.srfmolina.krocy.presentation.feature.spash.navigation.SplashRoute
import com.srfmolina.krocy.presentation.feature.spash.navigation.splashScreen
import com.srfmolina.krocy.presentation.feature.welcome.navigation.welcomeScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.appNavGraph(
    navController: NavController
){
    navigation<AppRoute>(
        startDestination = SplashRoute
    ) {
        welcomeScreen()

        splashScreen()
    }
}

@Serializable
data object AppRoute