package com.srfmolina.krocy.ui.presentation.feature.stock.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.feature.stock.StockScreen
import kotlinx.serialization.Serializable

@Serializable
data object StockRoute

fun NavController.navigateToStock(
    navOptions: NavOptions? = null
) = navigate(route = StockRoute, navOptions)

fun NavGraphBuilder.stockScreen() {
    composable<StockRoute> {
        StockScreen()
    }
}
