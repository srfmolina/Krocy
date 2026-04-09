package com.srfmolina.krocy.ui.presentation.feature.stock

import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.State
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi

internal class StockViewModel : BaseViewModel<Event, State, Effect>() {

    sealed interface Event: UiEvent {
        data object Init : Event
    }

    sealed interface Effect : UiEffect

    data class State(
        val isLoading: Boolean = true,
        val items: List<StockItemUi> = emptyList()
    ) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> init()
        }
    }

    private fun init() {

    }

}