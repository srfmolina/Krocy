package com.srfmolina.krocy.ui.presentation.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.common.AppIcon
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LoginScreen(
    onDemoServer: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.s4,
            alignment = Alignment.CenterVertically
        )
    ){
        AppIcon(120.dp)

        Text(
            text = "¡Bienvenido a Krocy!",
            style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.s4)
            ) {
                Text(
                    text = "Para utilizar Krocy necesitas tu propio servidor Grocy o la app para \"Home Assistant\". Si sólo quieres probar esta aplicación, puedes utilizar el servidor de prueba de Bern Bestel, el creador de Grocy.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(MaterialTheme.spacing.s4),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)) {
            OutlinedButton(
                shapes = ButtonDefaults.shapes(),
                onClick = onDemoServer,
            ) {
                Text("Servidor de prueba")
            }

            Button(
                shapes = ButtonDefaults.shapes(),
                onClick = {
                    //TODO
                },
            ) {
                Text("Servidor propio")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginScreenPreview() {
    KrocyTheme {
        Surface {
            LoginScreen(
                onDemoServer = {}
            )
        }
    }
}