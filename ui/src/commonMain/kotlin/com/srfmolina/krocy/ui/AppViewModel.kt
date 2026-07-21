package com.srfmolina.krocy.ui

import androidx.lifecycle.viewModelScope
import com.srfmolina.krocy.domain.usecase.login.GetServerConfigUseCase
import com.srfmolina.krocy.domain.usecase.login.LogoutUseCase
import com.srfmolina.krocy.domain.usecase.login.ObserveSessionExpiredUseCase
import com.srfmolina.krocy.domain.usecase.login.OpenSessionUseCase
import com.srfmolina.krocy.ui.AppViewModel.Effect
import com.srfmolina.krocy.ui.AppViewModel.Event
import com.srfmolina.krocy.ui.AppViewModel.State
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.common.model.DialogConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import kotlinx.coroutines.launch

internal class AppViewModel(
    private val getServerConfig: GetServerConfigUseCase,
    private val openSession: OpenSessionUseCase,
    private val logout: LogoutUseCase,
    private val observeSessionExpired: ObserveSessionExpiredUseCase
) : BaseViewModel<Event, State, Effect>() {

    sealed interface Event : UiEvent {
        data object Init : Event
        data object OnLogoutClick : Event
        data object OnLogoutConfirm : Event
        data class OnTopBarChange(val config: TopBarConfigurationUi) : Event
        data class OnChangeNavRailStatus(val open: Boolean) : Event
        data class OnFabChange(val config: FabConfigurationUi) : Event
        data class OnDialogChange(val config: DialogConfigurationUi?) : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToWelcome : Effect
        data object NavigateToStock : Effect
        data object NavigateToLogin : Effect
        data object ShowLogoutDialog : Effect
    }

    data class State(
        val isLoading: Boolean = true,
        val isNavRailOpen: Boolean = false,
        val topBarConfig: TopBarConfigurationUi? = null,
        val fabConfig: FabConfigurationUi? = null,
        val dialogConfig: DialogConfigurationUi? = null
    ) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> init()
            is Event.OnLogoutClick -> launchEffect(Effect.ShowLogoutDialog)
            is Event.OnLogoutConfirm -> confirmLogout()
            is Event.OnTopBarChange -> setState { copy(topBarConfig = event.config) }
            is Event.OnChangeNavRailStatus -> setState { copy(isNavRailOpen = event.open) }
            is Event.OnFabChange -> setState { copy(fabConfig = event.config) }
            is Event.OnDialogChange -> setState { copy(dialogConfig = event.config) }
        }
    }

    private suspend fun init() {
        watchSessionExpiry()
        val config = getServerConfig().getOrNull()
        val opened = config != null && openSession(config).isSuccess
        setState { copy(isLoading = false) }
        launchEffect(if (opened) Effect.NavigateToStock else Effect.NavigateToWelcome)
    }

    private suspend fun confirmLogout() {
        logout()
        setState { copy(dialogConfig = null, topBarConfig = null, fabConfig = null) }
        launchEffect(Effect.NavigateToLogin)
    }

    private fun watchSessionExpiry() {
        viewModelScope.launch {
            observeSessionExpired().collect { result ->
                if (result.isSuccess) {
                    logout()
                    setState { copy(dialogConfig = null, topBarConfig = null, fabConfig = null) }
                    launchEffect(Effect.NavigateToLogin)
                }
            }
        }
    }
}
