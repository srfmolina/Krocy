package com.srfmolina.krocy.ui.presentation.feature.login.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.ConnectionUi
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.Event
import com.srfmolina.krocy.ui.presentation.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ServerSetupScreen(
    onNavigateToStock: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ServerSetupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ServerSetupViewModel.Effect.NavigateToStock -> onNavigateToStock()
            }
        }
    }

    val isConnecting = state.connection is ConnectionUi.Connecting

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.s4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)
        ) {
            Text(
                text = "Conectar con tu servidor Grocy",
                style = MaterialTheme.typography.titleLarge
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.form.usingHass,
                    onCheckedChange = { viewModel.launchEvent(Event.OnToggleHass) },
                    enabled = !isConnecting
                )
                Text(
                    text = "Uso Grocy mediante el complemento de Home Assistant",
                    modifier = Modifier.padding(start = MaterialTheme.spacing.s2)
                )
            }

            OutlinedTextField(
                value = state.form.serverUrl,
                onValueChange = { viewModel.launchEvent(Event.OnServerUrlChange(it)) },
                label = {
                    Text(if (state.form.usingHass) "URL de Home Assistant" else "URL del servidor")
                },
                supportingText = {
                    val error = state.fieldErrors?.serverUrlError
                    if (error != null) Text(error)
                    else if (state.form.usingHass) Text("Ejemplo: http://homeassistant.local:8123")
                    else Text("Ejemplo: https://grocy.midominio.com")
                },
                isError = state.fieldErrors?.serverUrlError != null,
                enabled = !isConnecting,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.form.usingHass) {
                OutlinedTextField(
                    value = state.form.haToken,
                    onValueChange = { viewModel.launchEvent(Event.OnHaTokenChange(it)) },
                    label = { Text("Token de acceso de larga duración") },
                    supportingText = {
                        Text(state.fieldErrors?.haTokenError
                            ?: "Se crea en tu perfil de Home Assistant, sección Seguridad")
                    },
                    isError = state.fieldErrors?.haTokenError != null,
                    enabled = !isConnecting,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.form.ingressProxyId,
                    onValueChange = { viewModel.launchEvent(Event.OnProxyIdChange(it)) },
                    label = { Text("Identificador del proxy ingress") },
                    supportingText = {
                        Text(state.fieldErrors?.proxyIdError
                            ?: "Cadena larga visible en la URL al abrir Grocy dentro de Home Assistant")
                    },
                    isError = state.fieldErrors?.proxyIdError != null,
                    enabled = !isConnecting,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = state.form.apiKey,
                onValueChange = { viewModel.launchEvent(Event.OnApiKeyChange(it)) },
                label = { Text("Clave API de Grocy") },
                supportingText = {
                    Text(state.fieldErrors?.apiKeyError
                        ?: "Se crea en Grocy: menú de usuario → Administrar claves API")
                },
                isError = state.fieldErrors?.apiKeyError != null,
                enabled = !isConnecting,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            when (val connection = state.connection) {
                is ConnectionUi.Error -> Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.s3)) {
                        Text(connection.message, style = MaterialTheme.typography.bodyLarge)
                        connection.detail?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = MaterialTheme.spacing.s2)
                            )
                        }
                    }
                }
                is ConnectionUi.VersionWarning -> Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.s3),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
                    ) {
                        Text(
                            "El servidor funciona con Grocy ${connection.version}, " +
                                "una versión no probada con Krocy. Puede haber errores."
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)) {
                            OutlinedButton(onClick = {
                                viewModel.launchEvent(Event.OnDismissWarning)
                            }) { Text("Cancelar") }
                            Button(onClick = {
                                viewModel.launchEvent(Event.OnContinueAnyway)
                            }) { Text("Continuar de todos modos") }
                        }
                    }
                }
                else -> Unit
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    shapes = ButtonDefaults.shapes(),
                    onClick = onNavigateBack,
                    enabled = !isConnecting
                ) { Text("Volver") }

                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { viewModel.launchEvent(Event.OnConnectClick) },
                    enabled = !isConnecting
                ) {
                    if (isConnecting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = MaterialTheme.spacing.s2)
                        )
                    }
                    Text(if (isConnecting) "Conectando…" else "Conectar")
                }
            }
        }
    }
}
