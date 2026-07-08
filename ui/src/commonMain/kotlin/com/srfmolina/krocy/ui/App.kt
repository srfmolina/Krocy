package com.srfmolina.krocy.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.srfmolina.krocy.ui.AppViewModel.Effect
import com.srfmolina.krocy.ui.AppViewModel.Event
import com.srfmolina.krocy.ui.presentation.common.KrocyFabMenu
import com.srfmolina.krocy.ui.presentation.common.KrocySnackbar
import com.srfmolina.krocy.ui.presentation.common.KrocySnackbarVisuals
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.feature.welcome.navigation.navigateToWelcome
import com.srfmolina.krocy.ui.presentation.navigation.NavigationComponent
import com.srfmolina.krocy.ui.presentation.navigation.SplashRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.rail.KrocyNavigationRail
import com.srfmolina.krocy.ui.presentation.navigation.component.rail.model.appRailItems
import com.srfmolina.krocy.ui.presentation.navigation.component.rail.model.currentRailRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.MediumTopBar
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.SmallTopBar
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarTypeUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val viewModel: AppViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onShowSnackbar: (SnackbarConfigurationUi) -> Unit = { config ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss() // replace any visible one
            val result = snackbarHostState.showSnackbar(
                KrocySnackbarVisuals(config.message, config.type, config.action)
            )
            if (result == SnackbarResult.ActionPerformed) config.action?.onClick?.invoke()
        }
    }

    val scrollBehavior = when (state.topBarConfig?.type) {
        TopBarTypeUi.SMALL -> TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        TopBarTypeUi.MEDIUM -> TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        null -> null
    }

    val currentRailRoute = navController.currentRailRoute()

    KrocyTheme {

        LaunchedEffect(Unit) {
            viewModel.launchEvent(Event.Init)
        }

        LaunchedEffect(navController) {
            navController.currentBackStackEntryFlow.first() // The graph must be ready before we navigate...
            viewModel.effect.collect { effect ->
                when (effect) {
                    is Effect.NavigateToWelcome -> navController.navigateToWelcome(
                        navOptions = navOptions {
                            popUpTo<SplashRoute> {
                                inclusive = true
                            }
                        }
                    )
                }
            }
        }

        if (currentRailRoute != null) {
            KrocyNavigationRail(
                items = navController.appRailItems(),
                selectedRoute = currentRailRoute,
                compactExpanded = state.isNavRailOpen,
                onCompactDismiss = { viewModel.launchEvent(Event.OnChangeNavRailStatus(false)) },
            ) {
                MainContent(
                    scrollBehavior = scrollBehavior,
                    state = state,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    onOpenNavRail = { viewModel.launchEvent(Event.OnChangeNavRailStatus(true)) },
                    onChangeTopBar = { config ->
                        viewModel.launchEvent(Event.OnTopBarChange(config))
                    },
                    onChangeFab = { config ->
                        viewModel.launchEvent(Event.OnFabChange(config))
                    },
                    onShowSnackbar = onShowSnackbar
                )
            }
        } else {
            MainContent(
                scrollBehavior = scrollBehavior,
                state = state,
                navController = navController,
                snackbarHostState = snackbarHostState,
                onOpenNavRail = { viewModel.launchEvent(Event.OnChangeNavRailStatus(true)) },
                onChangeTopBar = { config ->
                    viewModel.launchEvent(Event.OnTopBarChange(config))
                },
                onChangeFab = { config ->
                    viewModel.launchEvent(Event.OnFabChange(config))
                },
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainContent(
    scrollBehavior: TopAppBarScrollBehavior?,
    state: AppViewModel.State,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    onOpenNavRail: () -> Unit,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().then(
            scrollBehavior?.let {
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } ?: Modifier
        ),
        topBar = state.topBarConfig?.let { config ->
            {
                scrollBehavior?.let {
                    when (config.type) {
                        TopBarTypeUi.SMALL -> SmallTopBar(
                            title = config.title,
                            scrollBehavior = scrollBehavior,
                            navigationAction = config.leadingAction,
                            trailingAction = config.trailingAction
                        )

                        TopBarTypeUi.MEDIUM -> MediumTopBar(
                            title = config.title,
                            scrollBehavior = scrollBehavior,
                            navigationAction = config.leadingAction,
                            action = config.trailingAction
                        )
                    }
                }
            }
        } ?: {},
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.s4),
            ) { data ->
                KrocySnackbar(data)
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavigationComponent(
                navController = navController,
                onChangeTopBar = onChangeTopBar,
                onChangeFab = onChangeFab,
                onOpenNavRail = onOpenNavRail,
                onShowSnackbar = onShowSnackbar
            )

            state.fabConfig?.let {
                KrocyFabMenu(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(MaterialTheme.spacing.s4),
                    visible = it.isVisible,
                    actions = it.actions
                )
            }
        }
    }
}
