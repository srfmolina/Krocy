package com.srfmolina.krocy

import com.srfmolina.krocy.AppViewModel.Effect
import com.srfmolina.krocy.AppViewModel.Event
import com.srfmolina.krocy.AppViewModel.State
import com.srfmolina.krocy.base.BaseViewModel
import com.srfmolina.krocy.base.UiEffect
import com.srfmolina.krocy.base.UiEvent
import com.srfmolina.krocy.base.UiState

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