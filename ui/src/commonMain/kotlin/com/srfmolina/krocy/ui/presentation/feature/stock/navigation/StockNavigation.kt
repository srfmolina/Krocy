package com.srfmolina.krocy.ui.presentation.feature.stock.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Kitchen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.common.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.stock.StockScreen
import com.srfmolina.krocy.ui.presentation.navigation.KrocyNavigationItem
import com.srfmolina.krocy.ui.presentation.navigation.StockRoute


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

internal fun NavController.stockNavigationItem() = KrocyNavigationItem(
    icon = Icons.Outlined.Kitchen,
    label = "Inventario",
    contentDescription = "Botón navegación Inventario",
    navigateTo = ::navigateToStock,
    route = StockRoute
)