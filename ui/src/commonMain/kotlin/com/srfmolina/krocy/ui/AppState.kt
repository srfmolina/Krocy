package com.srfmolina.krocy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

data class AppState(
    val navController: NavHostController
)

@Composable
fun rememberNavControllerState( //TODO: maybe we don't need this
    navController: NavHostController = rememberNavController()
): AppState = remember(navController) {
    AppState(
        navController = navController
    )
}
