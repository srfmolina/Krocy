package com.srfmolina.krocy.ui.presentation.feature.purchase.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseScreen
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.PURCHASED_PRODUCT_NAME_KEY
import com.srfmolina.krocy.ui.presentation.navigation.PurchaseRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi

internal fun NavController.navigateToPurchase(
    navOptions: NavOptions? = null
) = navigate(route = PurchaseRoute, navOptions)

internal fun NavGraphBuilder.purchaseScreen(
    navController: NavController,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit,
) {
    composable<PurchaseRoute> {
        PurchaseScreen(
            onBack = { navController.popBackStack() },
            onPurchaseRegistered = { name ->
                navController.previousBackStackEntry
                    ?.savedStateHandle?.set(PURCHASED_PRODUCT_NAME_KEY, name)
                navController.popBackStack()
            },
            onChangeTopBar = onChangeTopBar,
            onChangeFab = onChangeFab,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
