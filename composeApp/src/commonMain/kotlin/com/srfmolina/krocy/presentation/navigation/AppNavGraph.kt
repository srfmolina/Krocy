package com.srfmolina.krocy.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.srfmolina.krocy.presentation.feature.welcome.navigation.WelcomeRoute
import com.srfmolina.krocy.presentation.feature.welcome.navigation.welcomeScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.appNavGraph(
    navController: NavController
){
    navigation<AppRoute>(
        startDestination = WelcomeRoute
    ) {
        welcomeScreen()
    }
}

@Serializable
data object AppRoute