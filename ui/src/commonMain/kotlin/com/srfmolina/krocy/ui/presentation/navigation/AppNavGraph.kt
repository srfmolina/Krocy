package com.srfmolina.krocy.ui.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.srfmolina.krocy.ui.presentation.feature.login.navigation.loginScreen
import com.srfmolina.krocy.ui.presentation.feature.splash.navigation.splashScreen
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.stockScreen
import com.srfmolina.krocy.ui.presentation.feature.welcome.navigation.welcomeScreen
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import kotlinx.serialization.Serializable

internal fun NavGraphBuilder.appNavGraph(
    navController: NavController,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit
){
    navigation<AppRoute>(
        startDestination = SplashRoute
    ) {
        splashScreen()

        welcomeScreen(
            navController = navController
        )

        loginScreen(
            navController = navController
        )

        stockScreen(
            onChangeTopBar = onChangeTopBar
        )
    }
}

@Serializable
data object AppRoute