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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.domain.model.server.GrocyQrCredentials
import com.srfmolina.krocy.domain.model.server.isCleartextRisk
import com.srfmolina.krocy.ui.presentation.common.scanner.CameraQrScanner
import com.srfmolina.krocy.ui.presentation.common.scanner.isQrScannerSupported
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

            if (isQrScannerSupported) {
                OutlinedButton(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { viewModel.launchEvent(Event.OnScanQrClick) },
                    enabled = !isConnecting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.s2)
                    )
                    Text("Escanear código QR")
                }
            }

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
                    when {
                        error != null -> Text(error)
                        isCleartextRisk(state.form.serverUrl) -> Text(
                            text = "Conexión sin cifrar: tus credenciales viajarían visibles por la red.",
                            color = MaterialTheme.colorScheme.error
                        )
                        state.form.usingHass -> Text("Ejemplo: http://homeassistant.local:8123")
                        else -> Text("Ejemplo: https://grocy.midominio.com")
                    }
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
                    // Password keyboard: keeps the IME from learning the token or offering
                    // it later as an autocomplete suggestion (masking alone doesn't).
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                is ConnectionUi.QrConfirmation -> Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.s3),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
                    ) {
                        Text(
                            text = "Código QR leído",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val credentials = connection.credentials
                        val scannedUrl = when (credentials) {
                            is GrocyQrCredentials.SelfHosted -> credentials.serverUrl
                            is GrocyQrCredentials.HomeAssistant -> credentials.haServerUrl
                        }
                        // The authority (scheme + host + port) is the only part the user can
                        // actually judge, so it is the prominent element - a long path could
                        // otherwise push a look-alike host off the visible line.
                        val schemeSeparator = scannedUrl.indexOf("://")
                        val pathStart = if (schemeSeparator >= 0) {
                            scannedUrl.indexOf('/', schemeSeparator + "://".length)
                        } else {
                            -1
                        }
                        val authority = if (pathStart >= 0) scannedUrl.substring(0, pathStart) else scannedUrl
                        val path = if (pathStart >= 0) scannedUrl.substring(pathStart) else ""
                        Text(text = authority, style = MaterialTheme.typography.titleMedium)
                        if (path.isNotEmpty()) {
                            Text(
                                text = path,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (credentials is GrocyQrCredentials.HomeAssistant) {
                            Text(
                                text = "Modo Home Assistant. Comprueba que esta dirección es la " +
                                    "de tu servidor antes de introducir tu token de acceso de larga duración.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = "Comprueba que esta dirección es la de tu servidor.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (connection.isCleartext) {
                            Text(
                                text = "Conexión sin cifrar (http) a un servidor fuera de tu red " +
                                    "local: tus credenciales viajarían visibles por la red.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)) {
                            OutlinedButton(onClick = {
                                viewModel.launchEvent(Event.OnQrReject)
                            }) { Text("Cancelar") }
                            Button(onClick = {
                                viewModel.launchEvent(Event.OnQrConfirm)
                            }) { Text("Usar estos datos") }
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
                                .padding(end = MaterialTheme.spacing.s2)
                                .size(18.dp)
                        )
                    }
                    Text(if (isConnecting) "Conectando…" else "Conectar")
                }
            }
        }
    }

    if (state.isScanning) {
        Dialog(
            onDismissRequest = { viewModel.launchEvent(Event.OnQrScannerDismiss) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CameraQrScanner(
                onFrame = { frame -> viewModel.launchEvent(Event.OnQrFrame(frame)) },
                onDismiss = { viewModel.launchEvent(Event.OnQrScannerDismiss) }
            )
        }
    }
}
