package com.srfmolina.krocy.ui.presentation.feature.stock

import androidx.lifecycle.viewModelScope
import com.srfmolina.krocy.domain.usecase.stock.ObserveStockUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.State
import com.srfmolina.krocy.ui.presentation.feature.stock.mapper.toUi
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class StockViewModel(
    private val observeStockUseCase: ObserveStockUseCase
) : BaseViewModel<Event, State, Effect>() {

    sealed interface Event : UiEvent {
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
        getStock()
    }

    private fun getStock() = observeStockUseCase().onEach { result ->
        result.onSuccess { items ->
            setState { copy(isLoading = false, items = items.map { it.toUi() }) }
        }.onFailure {
            setState { copy(isLoading = false) }
        }
    }.launchIn(viewModelScope)
}