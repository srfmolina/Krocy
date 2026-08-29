package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.GrocyQrCredentials
import com.srfmolina.krocy.domain.model.server.QrFrame
import com.srfmolina.krocy.domain.model.server.QrScanOutcome
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.model.server.isCleartextRisk
import com.srfmolina.krocy.domain.usecase.login.CompleteLoginUseCase
import com.srfmolina.krocy.domain.usecase.login.ScanGrocyQrUseCase
import com.srfmolina.krocy.domain.usecase.login.ValidateServerUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.State

private const val ERROR_QR_NOT_GROCY = "El código QR no pertenece a un servidor Grocy"

internal class ServerSetupViewModel(
    private val validateServer: ValidateServerUseCase,
    private val completeLogin: CompleteLoginUseCase,
    private val scanGrocyQr: ScanGrocyQrUseCase
) : BaseViewModel<Event, State, Effect>() {

    sealed interface Event : UiEvent {
        data object OnToggleHass : Event
        data class OnServerUrlChange(val value: String) : Event
        data class OnApiKeyChange(val value: String) : Event
        data class OnHaTokenChange(val value: String) : Event
        data class OnProxyIdChange(val value: String) : Event
        data object OnConnectClick : Event
        data object OnContinueAnyway : Event
        data object OnDismissWarning : Event
        data object OnScanQrClick : Event
        data class OnQrFrame(val frame: QrFrame) : Event
        data object OnQrScannerDismiss : Event
        data object OnQrConfirm : Event
        data object OnQrReject : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToStock : Effect
    }

    sealed interface ConnectionUi {
        data object Idle : ConnectionUi
        data object Connecting : ConnectionUi
        data class Error(val message: String, val detail: String?) : ConnectionUi
        data class VersionWarning(val version: String) : ConnectionUi

        /**
         * A scan produced credentials, and the user has not vouched for them yet.
         *
         * Nothing is written to the form until they do. The host in a QR is chosen by whoever
         * printed it, and in Home Assistant mode the user is about to type a long-lived token
         * into a form pointed at that host - so the address gets shown and confirmed first.
         */
        data class QrConfirmation(
            val credentials: GrocyQrCredentials,
            val isCleartext: Boolean
        ) : ConnectionUi
    }

    data class State(
        val form: ServerSetupForm = ServerSetupForm(),
        val fieldErrors: ValidationResult.Invalid? = null,
        val connection: ConnectionUi = ConnectionUi.Idle,
        val isScanning: Boolean = false
    ) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.OnToggleHass -> updateForm { copy(usingHass = !usingHass) }
            is Event.OnServerUrlChange -> updateForm { copy(serverUrl = event.value) }
            is Event.OnApiKeyChange -> updateForm { copy(apiKey = event.value) }
            is Event.OnHaTokenChange -> updateForm { copy(haToken = event.value) }
            is Event.OnProxyIdChange -> updateForm { copy(ingressProxyId = event.value) }
            is Event.OnConnectClick -> connect()
            is Event.OnContinueAnyway -> pendingConfig?.let { finishLogin(it) }
            is Event.OnDismissWarning -> {
                pendingConfig = null
                setState { copy(connection = ConnectionUi.Idle) }
            }
            is Event.OnScanQrClick -> setState {
                copy(isScanning = true, fieldErrors = null, connection = ConnectionUi.Idle)
            }
            is Event.OnQrScannerDismiss -> setState { copy(isScanning = false) }
            is Event.OnQrFrame -> processQrFrame(event.frame)
            is Event.OnQrConfirm -> {
                val pending = currentState.connection as? ConnectionUi.QrConfirmation
                if (pending != null) {
                    setState {
                        copy(
                            form = form.applyQr(pending.credentials),
                            fieldErrors = null,
                            connection = ConnectionUi.Idle
                        )
                    }
                }
            }
            is Event.OnQrReject -> setState { copy(connection = ConnectionUi.Idle) }
        }
    }

    /** Config that passed validation but reported an unsupported version. */
    private var pendingConfig: ServerConfig? = null

    /**
     * The camera produces frames far faster than ZXing consumes them; without this guard every
     * dropped frame would still queue a decode and the backlog would outlive the scan.
     */
    private var decodingFrame = false

    private suspend fun processQrFrame(frame: QrFrame) {
        if (!currentState.isScanning || decodingFrame) return
        decodingFrame = true
        try {
            // A decoder crash is treated as "not a Grocy QR": the user gets an actionable
            // message instead of a silent camera that never resolves.
            val outcome = scanGrocyQr(frame).getOrElse { QrScanOutcome.NotGrocy }
            when (outcome) {
                is QrScanOutcome.NotFound -> Unit // still aiming
                is QrScanOutcome.NotGrocy -> setState {
                    copy(
                        isScanning = false,
                        connection = ConnectionUi.Error(ERROR_QR_NOT_GROCY, null)
                    )
                }
                is QrScanOutcome.Found -> {
                    val serverUrl = when (val credentials = outcome.credentials) {
                        is GrocyQrCredentials.SelfHosted -> credentials.serverUrl
                        is GrocyQrCredentials.HomeAssistant -> credentials.haServerUrl
                    }
                    setState {
                        copy(
                            isScanning = false,
                            connection = ConnectionUi.QrConfirmation(
                                credentials = outcome.credentials,
                                isCleartext = isCleartextRisk(serverUrl)
                            )
                        )
                    }
                }
            }
        } finally {
            decodingFrame = false
        }
    }

    private fun updateForm(reduce: ServerSetupForm.() -> ServerSetupForm) {
        setState { copy(form = form.reduce(), fieldErrors = null, connection = ConnectionUi.Idle) }
    }

    private suspend fun connect() {
        // Guards against a double-tap firing two validation network calls before
        // recomposition disables the button.
        if (currentState.connection is ConnectionUi.Connecting) return

        when (val validation = currentState.form.validate()) {
            is ValidationResult.Invalid -> setState { copy(fieldErrors = validation) }
            is ValidationResult.Valid -> {
                setState { copy(fieldErrors = null, connection = ConnectionUi.Connecting) }
                validateServer(validation.config).fold(
                    onSuccess = { serverValidation ->
                        if (serverValidation.isSupported) {
                            finishLogin(validation.config)
                        } else {
                            pendingConfig = validation.config
                            setState {
                                copy(connection = ConnectionUi.VersionWarning(serverValidation.grocyVersion))
                            }
                        }
                    },
                    onFailure = { failure ->
                        val loginMessage = failure.toLoginMessage()
                        setState {
                            copy(connection = ConnectionUi.Error(loginMessage.message, loginMessage.detail))
                        }
                    }
                )
            }
        }
    }

    private suspend fun finishLogin(config: ServerConfig) {
        setState { copy(connection = ConnectionUi.Connecting) }
        completeLogin(config).fold(
            onSuccess = { launchEffect(Effect.NavigateToStock) },
            onFailure = { failure ->
                val loginMessage = failure.toLoginMessage()
                setState {
                    copy(connection = ConnectionUi.Error(loginMessage.message, loginMessage.detail))
                }
            }
        )
    }
}
