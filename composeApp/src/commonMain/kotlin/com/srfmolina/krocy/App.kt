package com.srfmolina.krocy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srfmolina.krocy.presentation.navigation.NavigationComponent
import com.srfmolina.krocy.presentation.theme.KrocyTheme

@Composable
fun App() {
    KrocyTheme {

        val state = rememberAppState()

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
