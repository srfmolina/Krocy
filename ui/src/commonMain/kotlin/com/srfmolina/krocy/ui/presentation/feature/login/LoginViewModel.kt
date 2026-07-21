package com.srfmolina.krocy.ui.presentation.feature.login

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.usecase.login.CompleteLoginUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.feature.login.LoginViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.login.LoginViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.login.LoginViewModel.State

internal class LoginViewModel(
    private val completeLogin: CompleteLoginUseCase
) : BaseViewModel<Event, State, Effect>() {

    sealed interface Event : UiEvent {
        data object OnDemoServerClick : Event
        data object OnOwnServerClick : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToStock : Effect
        data object NavigateToServerSetup : Effect
        data class ShowError(val message: String) : Effect
    }

    data class State(val isConnecting: Boolean = false) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.OnDemoServerClick -> loginToDemo()
            is Event.OnOwnServerClick -> launchEffect(Effect.NavigateToServerSetup)
        }
    }

    private suspend fun loginToDemo() {
        setState { copy(isConnecting = true) }
        val result = completeLogin(ServerConfig.Demo)
        setState { copy(isConnecting = false) }
        if (result.isSuccess) {
            launchEffect(Effect.NavigateToStock)
        } else {
            launchEffect(Effect.ShowError("No se pudo preparar el servidor de prueba"))
        }
    }
}
