package com.srfmolina.krocy.ui.presentation.navigation.component.rail.model

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.stockNavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.KrocyRoute
import com.srfmolina.krocy.ui.presentation.navigation.NavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.StockRoute

private val railRoutes: List<KrocyRoute> = listOf(
    StockRoute,
    // Add new top-level destinations here
)

@Composable
internal fun NavController.currentRailRoute(): KrocyRoute? {
    val entry = currentBackStackEntryAsState().value ?: return null
    val route = entry.destination.route?.substringBefore("?") ?: return null
    return railRoutes.firstOrNull { it::class.qualifiedName == route }
}

internal fun NavHostController.appRailItems(): List<NavigationItemUi> = listOf(
    stockNavigationItemUi(),
    // Mirror railRoutes order here
)
