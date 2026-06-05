package com.srfmolina.krocy.ui.presentation.feature.creation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductScreen
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.CREATED_PRODUCT_NAME_KEY
import com.srfmolina.krocy.ui.presentation.navigation.CreateProductRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi

internal fun NavController.navigateToCreateProduct(
    navOptions: NavOptions? = null
) = navigate(route = CreateProductRoute, navOptions)

internal fun NavGraphBuilder.createProductScreen(
    navController: NavController,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
) {
    composable<CreateProductRoute> {
        CreateProductScreen(
            onBack = { navController.popBackStack() },
            onProductCreated = { name ->
                navController.previousBackStackEntry
                    ?.savedStateHandle?.set(CREATED_PRODUCT_NAME_KEY, name)
                navController.popBackStack()
            },
            onChangeTopBar = onChangeTopBar,
            onChangeFab = onChangeFab,
        )
    }
}
