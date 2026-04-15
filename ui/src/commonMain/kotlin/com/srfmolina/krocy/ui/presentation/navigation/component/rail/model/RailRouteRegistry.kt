package com.srfmolina.krocy.ui.presentation.navigation.component.rail.model

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.stockNavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.KrocyRoute
import com.srfmolina.krocy.ui.presentation.navigation.NavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.StockRoute

internal object RailRouteRegistry {

    private val routes: Map<String, KrocyRoute> = listOf(
        StockRoute
    ).associateBy { it::class.qualifiedName!! }

    fun fromRoute(route: String?): KrocyRoute? {
        return route?.substringBefore("?")?.let { routes[it] }
    }

    fun railItemsUi(navController: NavHostController): List<NavigationItemUi>
        = listOf(
            navController.stockNavigationItemUi()
        )
}

@Composable
internal fun NavController.currentRailRoute(): KrocyRoute? {
    val entry = currentBackStackEntryAsState().value
    return RailRouteRegistry.fromRoute(entry?.destination?.route)
}
