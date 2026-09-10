package com.srfmolina.krocy.ui.presentation.feature.stock

import androidx.lifecycle.viewModelScope
import com.srfmolina.krocy.domain.usecase.stock.AddStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ConsumeStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.LoadStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ObserveStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.OpenStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.RefreshStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.model.BasicStockUCRequest
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.State
import com.srfmolina.krocy.ui.presentation.feature.stock.mapper.toUi
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class StockViewModel(
    private val observeStockUseCase: ObserveStockUseCase,
    private val consumeStockUseCase: ConsumeStockUseCase,
    private val addStockUseCase: AddStockUseCase,
    private val openStockUseCase: OpenStockUseCase,
    private val refreshStockUseCase: RefreshStockUseCase,
    private val loadStockUseCase: LoadStockUseCase,
) : BaseViewModel<Event, State, Effect>() {

    /** Once-only latch for [init]; see there. */
    private val initialized = MutableStateFlow(false)

    sealed interface Event : UiEvent {
        data object Init : Event
        data object OnRefresh : Event
        data class OnConsumeOne(val productId: Int) : Event
        data class OnOpenOne(val productId: Int) : Event
        data class OnAddOne(val productId: Int) : Event
    }

    sealed interface Effect : UiEffect

    data class State(
        val isLoading: Boolean = true,
        val loadingItemIds: Set<Int> = emptySet(),
        val items: List<StockItemUi> = emptyList()
    ) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> init()
            is Event.OnConsumeOne -> consume(event.productId, 1)
            is Event.OnAddOne -> add(event.productId, 1)
            is Event.OnOpenOne -> open(event.productId, 1)
            is Event.OnRefresh -> refresh()
        }
    }

    private suspend fun init() {
        // LaunchedEffect(Unit) re-sends Init whenever the screen re-enters composition while
        // this ViewModel survives: a second collector would duplicate every emission.
        if (initialized.getAndUpdate { true }) return
        // One collection for the ViewModel's lifetime. The repository's stream never ends,
        // not even on a failed load, so a later OnRefresh reaches this same collector.
        getStock()
        loadStockUseCase().onFailure { setState { copy(isLoading = false) } }
    }

    private fun getStock() = observeStockUseCase().onEach { result ->
        result.onSuccess { items ->
            setState { copy(isLoading = false, items = items.map { it.toUi() }) }
        }.onFailure {
            setState { copy(isLoading = false) }
        }
    }.launchIn(viewModelScope)

    private suspend fun consume(productId: Int, amount: Int) {
        setState { copy(loadingItemIds = loadingItemIds + productId) }
        val result = consumeStockUseCase(BasicStockUCRequest(productId, amount))
        setState { copy(loadingItemIds = loadingItemIds - productId) }
        result.onFailure { /* TODO: surface error effect */ }
    }

    private suspend fun add(productId: Int, amount: Int) {
        setState { copy(loadingItemIds = loadingItemIds + productId) }
        val result = addStockUseCase(BasicStockUCRequest(productId, amount))
        setState { copy(loadingItemIds = loadingItemIds - productId) }
        result.onFailure { /* TODO: surface error effect */ }
    }

    private suspend fun open(productId: Int, amount: Int) {
        setState { copy(loadingItemIds = loadingItemIds + productId) }
        val result = openStockUseCase(BasicStockUCRequest(productId, amount))
        setState { copy(loadingItemIds = loadingItemIds - productId) }
        result.onFailure { /* TODO: surface error effect */ }
    }

    private suspend fun refresh() {
        setState { copy(isLoading = true) }
        refreshStockUseCase()
        setState { copy(isLoading = false) }
    }
}