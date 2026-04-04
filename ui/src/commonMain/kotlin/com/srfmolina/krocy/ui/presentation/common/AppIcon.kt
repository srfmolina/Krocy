package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppIcon(
    size: Dp
) {
    //TODO
    LoadingIndicator(modifier = Modifier.size(size))
}

@PreviewLightDark
@Composable
private fun AppIconPreview() {
    KrocyTheme {
        AppIcon(120.dp)
    }
}