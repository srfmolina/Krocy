package com.srfmolina.krocy.ui.presentation.feature.stock.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Kitchen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.stock.StockScreen
import com.srfmolina.krocy.ui.presentation.navigation.NavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.StockRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi

/** Navigation result key set by the create-product screen so Stock can confirm the creation. */
internal const val CREATED_PRODUCT_NAME_KEY = "created_product_name"

internal fun NavController.navigateToStock(
    navOptions: NavOptions? = null
) = navigate(route = StockRoute, navOptions)

internal fun NavGraphBuilder.stockScreen(
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onOpenNavRail: () -> Unit,
    onNavigateToCreateProduct: () -> Unit
) {
    composable<StockRoute> { entry ->
        val savedStateHandle = entry.savedStateHandle
        StockScreen(
            onChangeTopBar = onChangeTopBar,
            onChangeFab = onChangeFab,
            onOpenNavRail = onOpenNavRail,
            onNavigateToCreateProduct = onNavigateToCreateProduct,
            createdProductNameFlow = savedStateHandle.getStateFlow<String?>(CREATED_PRODUCT_NAME_KEY, null),
            onCreatedProductNameConsumed = { savedStateHandle[CREATED_PRODUCT_NAME_KEY] = null }
        )
    }
}

internal fun NavController.stockNavigationItemUi() = NavigationItemUi(
    icon = Icons.Outlined.Kitchen,
    label = "Inventario",
    contentDescription = "Botón navegación Inventario",
    navigateTo = ::navigateToStock,
    route = StockRoute
)