package com.srfmolina.krocy.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect> : ViewModel() {

    // --- INITIAL STATE ---
    abstract fun createInitialState(): State

    // --- STATE ---
    // StateFlow: always holds a value, the UI observes it with collectAsStateWithLifecycle()
    private val _state: MutableStateFlow<State> by lazy { MutableStateFlow(createInitialState()) }
    val state: StateFlow<State> get() = _state.asStateFlow()

    // Direct access to the current state (without collect). Prefer setState/getAndSetState:
    // a read here that is used after a suspension point can already be stale by then.
    protected val currentState: State get() = _state.value

    // --- EFFECT ---
    // Buffered channel: guarantees delivery even if the UI is not subscribed at that moment.
    // receiveAsFlow() ensures that only ONE collector receives each effect (one-shot).
    private val _effect: Channel<Effect> = Channel(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // --- EVENT ---
    // Single entry point from the UI
    fun launchEvent(event: Event) {
        viewModelScope.launch {
            handleEvent(event)
        }
    }

    // Each child ViewModel implements its own event-handling logic
    abstract suspend fun handleEvent(event: Event)

    // Protected helpers so child classes can update state and emit effects

    protected fun setState(reduce: State.() -> State) {
        _state.update { it.reduce() }
    }

    /**
     * Applies [reduce] and returns the state as it was *before* it ran.
     *
     * Use it to claim something out of the state and act on it across a suspension in one
     * atomic step - reading [currentState] and writing separately leaves a window in which
     * another event's handler, resumed in between, can act on the same value.
     *
     * [reduce] must stay pure: it can be re-run under contention.
     */
    protected fun getAndSetState(reduce: State.() -> State): State =
        _state.getAndUpdate { it.reduce() }

    protected fun launchEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
