package com.srfmolina.krocy.ui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.srfmolina.krocy.ui.presentation.common.model.TopBarConfigurationUi
import kotlinx.serialization.Serializable

internal sealed interface KrocyRoute

@Serializable
data object SplashRoute: KrocyRoute

@Serializable
data object WelcomeRoute: KrocyRoute

@Serializable
data object LoginRoute: KrocyRoute

@Serializable
data object StockRoute: KrocyRoute


internal object RailRouteRegistry {

    private val routes: Map<String, KrocyRoute> = listOf(
        StockRoute
    ).associateBy { it::class.qualifiedName!! }

    fun fromRoute(route: String?): KrocyRoute? {
        return route?.substringBefore("?")?.let { routes[it] }
    }
}

@Composable
internal fun NavController.currentRailRoute(): KrocyRoute? {
    val entry = currentBackStackEntryAsState().value
    return RailRouteRegistry.fromRoute(entry?.destination?.route)
}

@Composable
internal fun NavigationComponent(
    navController: NavHostController,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit
) {
    NavHost(navController = navController, startDestination = AppRoute) {
        appNavGraph(navController, onChangeTopBar)
    }
}
