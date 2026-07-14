package com.srfmolina.krocy.ui.presentation.feature.shoppinglist

import androidx.lifecycle.viewModelScope
import com.srfmolina.krocy.domain.usecase.product.GetProductsUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.ObserveShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.RefreshShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.SetEntryDoneUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.model.AddToShoppingListUCRequest
import com.srfmolina.krocy.domain.usecase.shoppinglist.model.SetEntryDoneUCRequest
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.common.mapper.toOption
import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.toOptionsUi
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel.State
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.mapper.toUi
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.mapper.withEntryDone
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingGroupUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex

internal class ShoppingListViewModel(
    private val observeShoppingListUseCase: ObserveShoppingListUseCase,
    private val refreshShoppingListUseCase: RefreshShoppingListUseCase,
    private val setEntryDoneUseCase: SetEntryDoneUseCase,
    private val addToShoppingListUseCase: AddToShoppingListUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : BaseViewModel<Event, State, Effect>() {

    private val submitLock = Mutex()
    private var observeJob: Job? = null

    sealed interface Event : UiEvent {
        data object Init : Event
        data object OnRefresh : Event
        data class OnToggleDone(val entryId: Int) : Event
        data object OnOpenAddDialog : Event
        data object OnDismissAddDialog : Event
        data object OnRetryLoadProducts : Event
        data class OnAddProductSelected(val id: Int?) : Event
        data class OnAddAmountChange(val value: String) : Event
        data object OnAddSubmit : Event
    }

    sealed interface Effect : UiEffect {
        data class ProductAdded(val productName: String) : Effect
        data class ShowError(val message: String) : Effect
    }

    /** Form state of the app-hosted "add to list" dialog; null while the dialog is hidden. */
    data class AddDialogUi(
        val products: OptionsUi = OptionsUi(),
        val selectedProductId: Int? = null,
        val amount: String = "1",
        val isSubmitting: Boolean = false,
    ) {
        val amountValid: Boolean
            get() = amount.toDoubleOrNull()?.let { it > 0.0 } == true

        val isValid: Boolean
            get() = selectedProductId != null && amountValid

        val selectedProductName: String?
            get() = products.options.firstOrNull { it.id == selectedProductId }?.label
    }

    data class State(
        val isLoading: Boolean = true,
        val loadError: Boolean = false,
        val groups: List<ShoppingGroupUi> = emptyList(),
        val addDialog: AddDialogUi? = null,
    ) : UiState

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> observe()
            is Event.OnRefresh -> refresh()
            is Event.OnToggleDone -> toggleDone(event.entryId)
            is Event.OnOpenAddDialog -> openAddDialog()
            is Event.OnDismissAddDialog -> setState { copy(addDialog = null) }
            is Event.OnRetryLoadProducts -> loadDialogProducts()
            is Event.OnAddProductSelected -> setState {
                copy(addDialog = addDialog?.copy(selectedProductId = event.id))
            }
            is Event.OnAddAmountChange -> setState {
                copy(addDialog = addDialog?.copy(amount = event.value))
            }
            is Event.OnAddSubmit -> submitAdd()
        }
    }

    private fun observe() {
        observeJob?.cancel()
        observeJob = observeShoppingListUseCase().onEach { result ->
            result.onSuccess { entries ->
                setState { copy(isLoading = false, loadError = false, groups = entries.toUi()) }
            }.onFailure {
                setState { copy(isLoading = false, loadError = true) }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun refresh() {
        setState { copy(isLoading = true, loadError = false) }
        refreshShoppingListUseCase().fold(
            // A failed observe flow has completed (its catch emitted once), so re-subscribe;
            // the repository's StateFlow replays the freshly refreshed list immediately.
            onSuccess = { observe() },
            onFailure = { setState { copy(isLoading = false, loadError = true) } },
        )
    }

    private suspend fun toggleDone(entryId: Int) {
        val entry = currentState.groups.asSequence()
            .flatMap { it.entries.asSequence() }
            .firstOrNull { it.id == entryId }
            ?: return
        val newDone = !entry.done

        // Optimistic: the cross-off animates immediately; the repository confirms with the
        // same value, and a failure reverts the flip (the cache never changed).
        setState { copy(groups = groups.withEntryDone(entryId, newDone)) }
        setEntryDoneUseCase(SetEntryDoneUCRequest(entryId, newDone)).onFailure {
            setState { copy(groups = groups.withEntryDone(entryId, entry.done)) }
            launchEffect(Effect.ShowError("No se pudo actualizar la lista"))
        }
    }

    private suspend fun openAddDialog() {
        setState { copy(addDialog = AddDialogUi()) }
        loadDialogProducts()
    }

    private suspend fun loadDialogProducts() {
        setState { copy(addDialog = addDialog?.copy(products = OptionsUi())) }
        val products = getProductsUseCase().toOptionsUi { it.toOption() }
        setState {
            // The dialog may have been dismissed while loading.
            if (addDialog == null) this else copy(addDialog = addDialog.copy(products = products))
        }
    }

    private suspend fun submitAdd() {
        val dialog = currentState.addDialog ?: return
        if (!dialog.isValid) return
        val productName = dialog.selectedProductName ?: return

        // Events are not serialized (each runs in its own coroutine), so a second OnAddSubmit
        // dispatched before recomposition disables the button must be rejected here.
        if (!submitLock.tryLock()) return
        try {
            // The dialog closes right away; the screen shows the loading skeleton until the
            // repository's post-add refresh lands in the observed cache.
            setState { copy(addDialog = null, isLoading = true) }
            addToShoppingListUseCase(
                AddToShoppingListUCRequest(
                    productId = dialog.selectedProductId!!,
                    amount = dialog.amount.toDouble(),
                )
            ).fold(
                onSuccess = {
                    // observe() normally cleared this already when the refreshed cache emitted;
                    // this covers an emission deduped by the StateFlow.
                    setState { copy(isLoading = false) }
                    launchEffect(Effect.ProductAdded(productName))
                },
                onFailure = {
                    setState { copy(isLoading = false) }
                    launchEffect(Effect.ShowError("No se pudo añadir el producto"))
                },
            )
        } finally {
            submitLock.unlock()
        }
    }
}
