package com.srfmolina.krocy.ui.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KrocyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    CompositionLocalProvider(
        LocalKrocySpacing provides KrocySpacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KrocyTypography,
            content = content
        )
    }
}

val LocalKrocySpacing = staticCompositionLocalOf { KrocySpacing() }

data class KrocySpacing(
    val s0: Dp = 2.dp,
    val s1: Dp = 4.dp,
    val s2: Dp = 8.dp,
    val s4: Dp = 16.dp,
    val s6: Dp = 24.dp,
    val s8: Dp = 32.dp,
    val s12: Dp = 48.dp
)

val MaterialTheme.spacing: KrocySpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalKrocySpacing.current