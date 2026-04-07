package com.srfmolina.krocy.ui.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.srfmolina.krocy.ui.presentation.feature.login.navigation.loginScreen
import com.srfmolina.krocy.ui.presentation.feature.splash.navigation.SplashRoute
import com.srfmolina.krocy.ui.presentation.feature.splash.navigation.splashScreen
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.stockScreen
import com.srfmolina.krocy.ui.presentation.feature.welcome.navigation.welcomeScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.appNavGraph(
    navController: NavController
){
    navigation<AppRoute>(
        startDestination = SplashRoute
    ) {
        splashScreen()

        welcomeScreen(
            navController = navController
        )

        loginScreen()

        stockScreen()
    }
}

@Serializable
data object AppRoute