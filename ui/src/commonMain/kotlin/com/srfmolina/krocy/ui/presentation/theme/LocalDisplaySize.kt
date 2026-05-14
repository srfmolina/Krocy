package com.srfmolina.krocy.ui.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.srfmolina.krocy.ui.presentation.common.model.DisplaySizeUi

val LocalDisplaySize = compositionLocalOf { DisplaySizeUi.S }

val MaterialTheme.displaySize: DisplaySizeUi
    @Composable
    @ReadOnlyComposable
    get() = LocalDisplaySize.current
