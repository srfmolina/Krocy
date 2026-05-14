package com.srfmolina.krocy.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navOptions
import com.srfmolina.krocy.ui.AppViewModel.Effect
import com.srfmolina.krocy.ui.AppViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.welcome.navigation.navigateToWelcome
import com.srfmolina.krocy.ui.presentation.navigation.NavigationComponent
import com.srfmolina.krocy.ui.presentation.navigation.SplashRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.rail.KrocyNavigationRail
import com.srfmolina.krocy.ui.presentation.navigation.component.rail.model.appRailItems
import com.srfmolina.krocy.ui.presentation.navigation.component.rail.model.currentRailRoute
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.MediumTopBar
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.SmallTopBar
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarTypeUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val viewModel: AppViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navControllerState = rememberNavControllerState()

    val scrollBehavior = when (state.topBarConfig?.type) {
        TopBarTypeUi.SMALL -> TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        TopBarTypeUi.MEDIUM -> TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        null -> null
    }

    val currentRailRoute = navControllerState.navController.currentRailRoute()

    KrocyTheme {

        LaunchedEffect(Unit) {
            viewModel.launchEvent(Event.Init)
        }

        LaunchedEffect(navControllerState.navController) {
            navControllerState.navController.currentBackStackEntryFlow.first() // The graph must be ready before we navigate...
            viewModel.effect.collect { effect ->
                when (effect) {
                    is Effect.NavigateToWelcome -> navControllerState.navController.navigateToWelcome(
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
                items = navControllerState.navController.appRailItems(),
                selectedRoute = currentRailRoute,
                compactExpanded = state.isNavRailOpen,
                onCompactDismiss = { viewModel.launchEvent(Event.OnChangeNavRailStatus(false)) },
            ) {
                MainContent(scrollBehavior, state, viewModel, navControllerState, onOpenNavRail = { viewModel.launchEvent(Event.OnChangeNavRailStatus(true)) })
            }
        } else {
            MainContent(scrollBehavior, state, viewModel, navControllerState, onOpenNavRail = { viewModel.launchEvent(Event.OnChangeNavRailStatus(true)) })
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainContent(
    scrollBehavior: TopAppBarScrollBehavior?,
    vmState: AppViewModel.State,
    viewModel: AppViewModel,
    state: AppState,
    onOpenNavRail: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().then(
            scrollBehavior?.let {
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } ?: Modifier
        ),
        topBar = vmState.topBarConfig?.let { config ->
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
        } ?: {}
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavigationComponent(
                navController = state.navController,
                onChangeTopBar = { config ->
                    viewModel.launchEvent(Event.OnTopBarChange(config))
                },
                onOpenNavRail = onOpenNavRail
            )
        }
    }
}
