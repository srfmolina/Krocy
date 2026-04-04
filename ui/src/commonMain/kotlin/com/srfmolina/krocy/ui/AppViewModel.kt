package com.srfmolina.krocy.ui

import com.srfmolina.krocy.ui.AppViewModel.Effect
import com.srfmolina.krocy.ui.AppViewModel.Event
import com.srfmolina.krocy.ui.AppViewModel.State
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState

class AppViewModel : BaseViewModel<Event, State, Effect>() {

    sealed interface Event: UiEvent {
        data object Init : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToWelcome : Effect
    }

    data class State(
        val isLoading: Boolean = true
    ) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> init()
        }
    }

    private fun init() {
        //TODO: check logins...
        setState { copy(isLoading = false) }
        launchEffect(Effect.NavigateToWelcome) // If the user is not logged
    }

}