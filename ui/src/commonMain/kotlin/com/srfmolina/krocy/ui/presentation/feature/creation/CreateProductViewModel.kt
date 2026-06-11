package com.srfmolina.krocy.ui.presentation.feature.creation

import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateProductUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.State

internal class CreateProductViewModel(
    private val createProductUseCase: CreateProductUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getProductGroupsUseCase: GetProductGroupsUseCase,
) : BaseViewModel<Event, State, Effect>() {

    sealed interface Event : UiEvent {
        data class OnNameChange(val value: String) : Event
        data class OnDescriptionChange(val value: String) : Event
        data class OnStockUnitSelected(val id: Int) : Event
        data class OnPurchaseUnitSelected(val id: Int) : Event
        data class OnLocationSelected(val id: Int) : Event
        data class OnProductGroupSelected(val id: Int?) : Event
        data class OnMinStockChange(val value: String) : Event
        data object OnSubmit : Event
    }

    sealed interface Effect : UiEffect {
        data class ProductCreated(val name: String) : Effect
        data class ShowError(val message: String) : Effect
    }

    data class State(
        val quantityUnits: List<SelectableOptionUi> = emptyList(),
        val locations: List<SelectableOptionUi> = emptyList(),
        val productGroups: List<SelectableOptionUi> = emptyList(),
        val name: String = "",
        val description: String = "",
        val selectedStockUnitId: Int? = null,
        val selectedPurchaseUnitId: Int? = null,
        val selectedLocationId: Int? = null,
        val selectedProductGroupId: Int? = null,
        val minStockAmount: String = "",
        val showNameError: Boolean = false,
        val isSubmitting: Boolean = false,
    ) : UiState {
        val minStockValid: Boolean
            get() = minStockAmount.isBlank() || minStockAmount.toDoubleOrNull() != null

        val isValid: Boolean
            get() = name.isNotBlank() && selectedStockUnitId != null && selectedPurchaseUnitId != null &&
                selectedLocationId != null && minStockValid
    }

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.OnNameChange -> setState {
                copy(name = event.value, showNameError = showNameError && event.value.isBlank())
            }
            is Event.OnDescriptionChange -> setState { copy(description = event.value) }
            is Event.OnStockUnitSelected -> setState { copy(selectedStockUnitId = event.id) }
            is Event.OnPurchaseUnitSelected -> setState { copy(selectedPurchaseUnitId = event.id) }
            is Event.OnLocationSelected -> setState { copy(selectedLocationId = event.id) }
            is Event.OnProductGroupSelected -> setState { copy(selectedProductGroupId = event.id) }
            is Event.OnMinStockChange -> setState { copy(minStockAmount = event.value) }
            is Event.OnSubmit -> submit()
        }
    }

    private suspend fun submit() {
        val current = currentState
        if (!current.isValid) {
            setState { copy(showNameError = name.isBlank()) }
            return
        }

        setState { copy(isSubmitting = true) }
        val result = createProductUseCase(
            NewProduct(
                name = current.name.trim(),
                quIdStock = current.selectedStockUnitId!!,
                quIdPurchase = current.selectedPurchaseUnitId!!,
                locationId = current.selectedLocationId!!,
                description = current.description.ifBlank { null },
                minStockAmount = current.minStockAmount.toDoubleOrNull(),
                productGroupId = current.selectedProductGroupId,
            )
        )
        setState { copy(isSubmitting = false) }
        result
            .onSuccess { launchEffect(Effect.ProductCreated(current.name.trim())) }
            .onFailure { launchEffect(Effect.ShowError("No se pudo crear el producto")) }
    }
}
