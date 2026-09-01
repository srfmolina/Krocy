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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.sync.Mutex

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
        /**
         * [form] is what the tapped button was rendering, so the handler never has to read it
         * back out of the state.
         */
        data class OnConnectClick(val form: ServerSetupForm) : Event
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
            is Event.OnConnectClick -> connect(event.form)
            is Event.OnContinueAnyway -> continueAnyway()
            is Event.OnDismissWarning -> {
                pendingConfig.value = null
                setState { copy(connection = ConnectionUi.Idle) }
            }
            is Event.OnScanQrClick -> setState {
                copy(isScanning = true, fieldErrors = null, connection = ConnectionUi.Idle)
            }
            is Event.OnQrScannerDismiss -> setState { copy(isScanning = false) }
            is Event.OnQrFrame -> processQrFrame(event.frame)
            // Reading the pending confirmation inside the reducer keeps the check and the
            // write atomic; splitting them would leave a window for another handler to
            // replace what the user actually confirmed.
            is Event.OnQrConfirm -> setState {
                val pending = connection as? ConnectionUi.QrConfirmation ?: return@setState this
                copy(
                    form = form.applyQr(pending.credentials),
                    fieldErrors = null,
                    connection = ConnectionUi.Idle
                )
            }
            is Event.OnQrReject -> setState { copy(connection = ConnectionUi.Idle) }
        }
    }

    /**
     * Config that passed validation but reported an unsupported version.
     *
     * Held outside [State] because it is transaction state, not something the screen renders.
     * `getAndUpdate { null }` takes it and clears it in one step, so a double-tap on "continue
     * anyway" cannot start two logins.
     */
    private val pendingConfig = MutableStateFlow<ServerConfig?>(null)

    /**
     * The camera produces frames far faster than ZXing consumes them; without this guard every
     * dropped frame would still queue a decode and the backlog would outlive the scan.
     */
    private val decodeLock = Mutex()

    /** Serialises login attempts, whichever button started them. */
    private val connectLock = Mutex()

    private suspend fun processQrFrame(frame: QrFrame) {
        // A cheap pre-check that only saves work: if it is stale the decode is wasted, but
        // the reducers below still refuse to act on a scanner the user has closed.
        if (!currentState.isScanning) return
        if (!decodeLock.tryLock()) return
        try {
            // A decoder crash is treated as "not a Grocy QR": the user gets an actionable
            // message instead of a silent camera that never resolves.
            val outcome = scanGrocyQr(frame).getOrElse { QrScanOutcome.NotGrocy }
            // Events run in their own coroutines, so the scanner can be dismissed while this
            // decode is in flight. A result that lands afterwards belongs to a scan the user
            // already closed and must not reopen it as a confirmation or an error - so every
            // branch re-checks isScanning inside the reducer, atomically with its write.
            when (outcome) {
                is QrScanOutcome.NotFound -> Unit // still aiming
                is QrScanOutcome.NotGrocy -> setState {
                    if (!isScanning) return@setState this
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
                    val isCleartext = isCleartextRisk(serverUrl)
                    setState {
                        if (!isScanning) return@setState this
                        copy(
                            isScanning = false,
                            connection = ConnectionUi.QrConfirmation(
                                credentials = outcome.credentials,
                                isCleartext = isCleartext
                            )
                        )
                    }
                }
            }
        } finally {
            decodeLock.unlock()
        }
    }

    private fun updateForm(reduce: ServerSetupForm.() -> ServerSetupForm) {
        setState {
            copy(
                form = form.reduce(),
                fieldErrors = null,
                // A pending QR confirmation survives a keystroke elsewhere in the form; only
                // dropping otherwise clears connection state (an error, a version warning...).
                connection = if (connection is ConnectionUi.QrConfirmation) connection else ConnectionUi.Idle
            )
        }
    }

    private suspend fun connect(form: ServerSetupForm) {
        // Guards against a double-tap firing two validation network calls before recomposition
        // disables the button. connection is for rendering only - guarding on it would depend
        // on the read and the write never being split by a suspension.
        if (!connectLock.tryLock()) return
        try {
            when (val validation = form.validate()) {
                is ValidationResult.Invalid -> setState { copy(fieldErrors = validation) }
                is ValidationResult.Valid -> {
                    setState { copy(fieldErrors = null, connection = ConnectionUi.Connecting) }
                    validateServer(validation.config).fold(
                        onSuccess = { serverValidation ->
                            if (serverValidation.isSupported) {
                                finishLogin(validation.config)
                            } else {
                                pendingConfig.value = validation.config
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
        } finally {
            connectLock.unlock()
        }
    }

    private suspend fun continueAnyway() {
        if (!connectLock.tryLock()) return
        try {
            // Taking the config and clearing it in one step: a second tap finds nothing left.
            val config = pendingConfig.getAndUpdate { null } ?: return
            finishLogin(config)
        } finally {
            connectLock.unlock()
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
