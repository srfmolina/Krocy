package com.srfmolina.krocy.ui.presentation.feature.stock.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.common.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.stock.StockScreen
import kotlinx.serialization.Serializable

@Serializable
data object StockRoute

internal fun NavController.navigateToStock(
    navOptions: NavOptions? = null
) = navigate(route = StockRoute, navOptions)

internal fun NavGraphBuilder.stockScreen(
    onChangeTopBar: (TopBarConfigurationUi) -> Unit
) {
    composable<StockRoute> {
        StockScreen(
            onChangeTopBar = onChangeTopBar
        )
    }
}
