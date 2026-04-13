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
import com.srfmolina.krocy.ui.presentation.common.KrocyNavigationRail
import com.srfmolina.krocy.ui.presentation.common.MediumTopBar
import com.srfmolina.krocy.ui.presentation.common.SmallTopBar
import com.srfmolina.krocy.ui.presentation.common.model.TopBarType
import com.srfmolina.krocy.ui.presentation.feature.stock.navigation.stockNavigationItem
import com.srfmolina.krocy.ui.presentation.feature.welcome.navigation.navigateToWelcome
import com.srfmolina.krocy.ui.presentation.navigation.KrocyNavigationItem
import com.srfmolina.krocy.ui.presentation.navigation.NavigationComponent
import com.srfmolina.krocy.ui.presentation.navigation.SplashRoute
import com.srfmolina.krocy.ui.presentation.navigation.currentRailRoute
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val viewModel: AppViewModel = koinViewModel()
    val vmState by viewModel.state.collectAsStateWithLifecycle()
    val state = rememberAppState()

    val scrollBehavior = when (vmState.topBarConfig?.type) {
        TopBarType.SMALL -> TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        TopBarType.MEDIUM -> TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        null -> null
    }

    val currentRailRoute = state.navController.currentRailRoute()
    val railItems: List<KrocyNavigationItem> = listOf(
        state.navController.stockNavigationItem()
    )

    KrocyTheme {

        LaunchedEffect(Unit) {
            viewModel.launchEvent(Event.Init)
        }

        LaunchedEffect(state.navController) {
            state.navController.currentBackStackEntryFlow.first() // The graph must be ready before we navigate...
            viewModel.effect.collect { effect ->
                when (effect) {
                    is Effect.NavigateToWelcome -> state.navController.navigateToWelcome(
                        navOptions = navOptions {
                            popUpTo<SplashRoute> {
                                inclusive = true
                            }
                        }
                    )
                    is Effect.NavigateBack -> state.navController.popBackStack()
                }
            }
        }

        if (currentRailRoute != null) {
            KrocyNavigationRail(
                items = railItems,
                selectedRoute = currentRailRoute,
                content = { MainContent(scrollBehavior, vmState, viewModel, state) }
            )
        } else {
            MainContent(scrollBehavior, vmState, viewModel, state)
        }

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainContent(
    scrollBehavior: TopAppBarScrollBehavior?,
    vmState: AppViewModel.State,
    viewModel: AppViewModel,
    state: AppState
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
                        TopBarType.SMALL -> SmallTopBar(
                            title = config.title,
                            scrollBehavior = scrollBehavior,
                            onBack = { viewModel.launchEvent(Event.OnBack) },
                            action = config.action
                        )

                        TopBarType.MEDIUM -> MediumTopBar(
                            title = config.title,
                            scrollBehavior = scrollBehavior,
                            onBack = { viewModel.launchEvent(Event.OnBack) },
                            action = config.action
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
                }
            )
        }
    }
}
