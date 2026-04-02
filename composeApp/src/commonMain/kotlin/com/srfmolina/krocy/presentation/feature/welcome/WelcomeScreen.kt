package com.srfmolina.krocy.presentation.feature.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.presentation.common.AppIcon
import com.srfmolina.krocy.presentation.common.DotsPager
import com.srfmolina.krocy.presentation.theme.KrocyTheme
import com.srfmolina.krocy.presentation.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun WelcomeScreen() {
    DotsPager(
        modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.s4), // <- ahora sí tiene alto definido
        pageCount = 1
    ) { page ->
        WelcomePages(page, {})
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WelcomePages(
    page: Int,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (page) {
            0 -> WelcomeOne()
        }

        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            shapes = ButtonDefaults.shapes(),
            onClick = onStart
        ) {
            Text("Empezar")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WelcomeOne() {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s12),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppIcon(120)

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Krocy, tu cliente Grocy",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Maneja tu hogar al estilo almacen",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WelcomeScreenPreview() {
    KrocyTheme {
        Surface {
            WelcomeScreen()
        }
    }
}

@PreviewLightDark
@Composable
private fun WelcomeOnePreview() {
    KrocyTheme {
        WelcomeOne()
    }
}