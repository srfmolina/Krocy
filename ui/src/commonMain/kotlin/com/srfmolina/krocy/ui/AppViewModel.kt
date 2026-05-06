package com.srfmolina.krocy.ui

import com.srfmolina.krocy.ui.AppViewModel.Effect
import com.srfmolina.krocy.ui.AppViewModel.Event
import com.srfmolina.krocy.ui.AppViewModel.State
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi

internal class AppViewModel : BaseViewModel<Event, State, Effect>() {

    sealed interface Event: UiEvent {
        data object Init : Event
        data class OnTopBarChange(val config: TopBarConfigurationUi) : Event
        data class OnChangeNavRailStatus(val open: Boolean) : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToWelcome : Effect
    }

    data class State(
        val isLoading: Boolean = true,
        val isNavRailOpen: Boolean = false,
        val topBarConfig: TopBarConfigurationUi? = null
    ) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> init()
            is Event.OnTopBarChange -> setState { copy(topBarConfig = event.config) }
            is Event.OnChangeNavRailStatus -> setState { copy(isNavRailOpen = event.open) }
        }
    }

    private fun init() {
        //TODO: check logins...
        setState { copy(isLoading = false) }
        launchEffect(Effect.NavigateToWelcome) // If the user is not logged
    }

}