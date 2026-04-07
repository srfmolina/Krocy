package com.srfmolina.krocy.ui.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalExtendedColorScheme = staticCompositionLocalOf { extendedLight }

val MaterialTheme.extendedColorScheme: ExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current