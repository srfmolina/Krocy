package com.srfmolina.krocy.ui.presentation.feature.shoppinglist.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.common.model.DialogConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListScreen
import com.srfmolina.krocy.ui.presentation.navigation.NavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.ShoppingListRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi

internal fun NavController.navigateToShoppingList(
    navOptions: NavOptions? = null
) = navigate(route = ShoppingListRoute, navOptions)

internal fun NavGraphBuilder.shoppingListScreen(
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onChangeDialog: (DialogConfigurationUi?) -> Unit,
    onOpenNavRail: () -> Unit,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit
) {
    composable<ShoppingListRoute> {
        ShoppingListScreen(
            onChangeTopBar = onChangeTopBar,
            onChangeFab = onChangeFab,
            onChangeDialog = onChangeDialog,
            onOpenNavRail = onOpenNavRail,
            onShowSnackbar = onShowSnackbar
        )
    }
}

internal fun NavController.shoppingListNavigationItemUi() = NavigationItemUi(
    icon = Icons.Outlined.ShoppingCart,
    label = "Compra",
    contentDescription = "Botón navegación Lista de la compra",
    navigateTo = ::navigateToShoppingList,
    route = ShoppingListRoute
)
