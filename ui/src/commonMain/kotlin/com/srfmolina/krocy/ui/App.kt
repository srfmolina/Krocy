package com.srfmolina.krocy.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navOptions
import com.srfmolina.krocy.ui.AppViewModel.Effect
import com.srfmolina.krocy.ui.AppViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.splash.navigation.SplashRoute
import com.srfmolina.krocy.ui.presentation.feature.welcome.navigation.navigateToWelcome
import com.srfmolina.krocy.ui.presentation.navigation.NavigationComponent
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun App() {

    val viewModel: AppViewModel = koinViewModel()
    val vmState by viewModel.state.collectAsStateWithLifecycle()
    val state = rememberAppState()

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
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                NavigationComponent(
                    navController = state.navController
                )
            }
        }
    }
}
