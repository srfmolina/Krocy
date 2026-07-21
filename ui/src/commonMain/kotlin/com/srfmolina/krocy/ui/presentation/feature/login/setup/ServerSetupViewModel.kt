package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.usecase.login.CompleteLoginUseCase
import com.srfmolina.krocy.domain.usecase.login.ValidateServerUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.State

internal class ServerSetupViewModel(
    private val validateServer: ValidateServerUseCase,
    private val completeLogin: CompleteLoginUseCase
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
    }

    sealed interface Effect : UiEffect {
        data object NavigateToStock : Effect
    }

    sealed interface ConnectionUi {
        data object Idle : ConnectionUi
        data object Connecting : ConnectionUi
        data class Error(val message: String, val detail: String?) : ConnectionUi
        data class VersionWarning(val version: String) : ConnectionUi
    }

    data class State(
        val form: ServerSetupForm = ServerSetupForm(),
        val fieldErrors: ValidationResult.Invalid? = null,
        val connection: ConnectionUi = ConnectionUi.Idle
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
        }
    }

    /** Config that passed validation but reported an unsupported version. */
    private var pendingConfig: ServerConfig? = null

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
