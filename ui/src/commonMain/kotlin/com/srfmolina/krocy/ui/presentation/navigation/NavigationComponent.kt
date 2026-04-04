package com.srfmolina.krocy.ui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun NavigationComponent(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = AppRoute) {
        appNavGraph(navController)
    }
}