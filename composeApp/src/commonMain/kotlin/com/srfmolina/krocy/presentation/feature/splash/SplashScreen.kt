package com.srfmolina.krocy.presentation.feature.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.presentation.common.AppIcon
import com.srfmolina.krocy.presentation.theme.KrocyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(120)
        }
    }
}

@PreviewLightDark
@Composable
private fun SplashScreenPreview() {
    KrocyTheme {
        SplashScreen()
    }
}