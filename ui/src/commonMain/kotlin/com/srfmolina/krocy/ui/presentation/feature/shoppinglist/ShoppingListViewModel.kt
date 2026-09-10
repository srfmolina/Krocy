package com.srfmolina.krocy.ui.presentation.feature.shoppinglist

import androidx.lifecycle.viewModelScope
import com.srfmolina.krocy.domain.usecase.product.GetProductsUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.LoadShoppingListUseCase
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class ShoppingListViewModel(
    private val observeShoppingListUseCase: ObserveShoppingListUseCase,
    private val loadShoppingListUseCase: LoadShoppingListUseCase,
    private val refreshShoppingListUseCase: RefreshShoppingListUseCase,
    private val setEntryDoneUseCase: SetEntryDoneUseCase,
    private val addToShoppingListUseCase: AddToShoppingListUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : BaseViewModel<Event, State, Effect>() {

    /** Once-only latch for [start]; see there. */
    private val initialized = MutableStateFlow(false)

    /** Entry ids with a done/undone write in flight; see [toggleDone]. */
    private val togglesInFlight = MutableStateFlow<Set<Int>>(emptySet())

    sealed interface Event : UiEvent {
        data object Init : Event
        data object OnRefresh : Event
        /**
         * [done] is the value the tapped row was rendering, so the handler never has to read
         * it back out of the state after a suspension.
         */
        data class OnToggleDone(val entryId: Int, val done: Boolean) : Event
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
    ) {
        val amountValid: Boolean
            get() = amount.toDoubleOrNull()?.let { it > 0.0 } == true

        val isValid: Boolean
            get() = selectedProductId != null && amountValid

        val selectedProductName: String?
            get() = products.options.firstOrNull { it.id == selectedProductId }?.label

        /** Everything the submit needs is present, including a name to report back. */
        val isSubmittable: Boolean
            get() = isValid && selectedProductName != null
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
            is Event.Init -> start()
            is Event.OnRefresh -> refresh()
            is Event.OnToggleDone -> toggleDone(event.entryId, event.done)
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

    private suspend fun start() {
        // LaunchedEffect(Unit) re-sends Init whenever the screen re-enters composition while
        // this ViewModel survives: a second collector would duplicate every emission.
        if (initialized.getAndUpdate { true }) return
        // One collection for the ViewModel's lifetime. The repository's stream never ends,
        // not even on a failed load, so nothing ever needs to re-subscribe.
        observeShoppingListUseCase().onEach { result ->
            result.onSuccess { entries ->
                setState { copy(isLoading = false, loadError = false, groups = entries.toUi()) }
            }.onFailure {
                setState { copy(isLoading = false, loadError = true) }
            }
        }.launchIn(viewModelScope)
        loadShoppingListUseCase().onFailure {
            setState { copy(isLoading = false, loadError = true) }
        }
    }

    private suspend fun refresh() {
        setState { copy(isLoading = true, loadError = false) }
        refreshShoppingListUseCase().fold(
            // The collector started in Init receives the refreshed list. Clearing isLoading
            // here covers a refresh that changed nothing: the StateFlow dedupes an equal list,
            // so no emission arrives to clear it.
            onSuccess = { setState { copy(isLoading = false) } },
            onFailure = { setState { copy(isLoading = false, loadError = true) } },
        )
    }

    private suspend fun toggleDone(entryId: Int, done: Boolean) {
        // A second tap on the same entry while its write is still in flight has to be dropped.
        // Both handlers would otherwise flip against the same pre-toggle value, and the later
        // revert would restore it on top of the earlier one - leaving the row showing a state
        // the server never accepted. getAndUpdate claims the entry atomically, so the guard
        // does not depend on events happening to run on a confined dispatcher.
        val inFlight = togglesInFlight.getAndUpdate { it + entryId }
        if (entryId in inFlight) return

        val newDone = !done
        try {
            // Optimistic: the cross-off animates immediately; the repository confirms with the
            // same value, and a failure reverts to the value the tap was made against (the
            // cache never changed).
            setState { copy(groups = groups.withEntryDone(entryId, newDone)) }
            setEntryDoneUseCase(SetEntryDoneUCRequest(entryId, newDone)).onFailure {
                setState { copy(groups = groups.withEntryDone(entryId, done)) }
                launchEffect(Effect.ShowError("No se pudo actualizar la lista"))
            }
        } finally {
            togglesInFlight.update { it - entryId }
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
        // Claiming the dialog and closing it in one step makes this idempotent: a second
        // OnAddSubmit dispatched before recomposition disables the button finds nothing left
        // to claim. Reading it and closing it separately would rely on the two never being
        // split by a suspension. An incomplete dialog is left open instead.
        //
        // The dialog closes right away; the screen shows the loading skeleton until the
        // repository's post-add refresh lands in the observed cache.
        val dialog = getAndSetState {
            if (addDialog?.isSubmittable == true) copy(addDialog = null, isLoading = true) else this
        }.addDialog?.takeIf { it.isSubmittable } ?: return
        val productName = dialog.selectedProductName ?: return
        addToShoppingListUseCase(
            AddToShoppingListUCRequest(
                productId = dialog.selectedProductId!!,
                amount = dialog.amount.toDouble(),
            )
        ).fold(
            onSuccess = {
                // The Init collector normally cleared this already when the cache emitted;
                // this covers an emission deduped by the StateFlow.
                setState { copy(isLoading = false) }
                launchEffect(Effect.ProductAdded(productName))
            },
            onFailure = {
                setState { copy(isLoading = false) }
                launchEffect(Effect.ShowError("No se pudo añadir el producto"))
            },
        )
    }
}
