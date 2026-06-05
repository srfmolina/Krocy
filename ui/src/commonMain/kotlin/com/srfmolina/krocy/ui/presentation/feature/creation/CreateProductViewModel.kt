package com.srfmolina.krocy.ui.presentation.feature.creation

import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuantityUnitsUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateProductUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.State
import com.srfmolina.krocy.ui.presentation.feature.creation.mapper.toOption
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class CreateProductViewModel(
    private val createProductUseCase: CreateProductUseCase,
    private val getQuantityUnitsUseCase: GetQuantityUnitsUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getProductGroupsUseCase: GetProductGroupsUseCase,
) : BaseViewModel<Event, State, Effect>() {

    sealed interface Event : UiEvent {
        data object Init : Event
        data object OnRetryLoadOptions : Event
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
        val isLoadingOptions: Boolean = true,
        val optionsError: Boolean = false,
        val quantityUnits: List<SelectableOptionUi> = emptyList(),
        val locations: List<SelectableOptionUi> = emptyList(),
        val productGroups: List<SelectableOptionUi> = emptyList(),
        val name: String = "",
        val description: String = "",
        val stockUnitId: Int? = null,
        val purchaseUnitId: Int? = null,
        val locationId: Int? = null,
        val productGroupId: Int? = null,
        val minStockAmount: String = "",
        val showNameError: Boolean = false,
        val isSubmitting: Boolean = false,
    ) : UiState {
        val minStockValid: Boolean
            get() = minStockAmount.isBlank() || minStockAmount.toDoubleOrNull() != null

        val isValid: Boolean
            get() = name.isNotBlank() && stockUnitId != null && purchaseUnitId != null &&
                locationId != null && minStockValid
    }

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> loadOptions()
            is Event.OnRetryLoadOptions -> loadOptions()
            is Event.OnNameChange -> setState {
                copy(name = event.value, showNameError = showNameError && event.value.isBlank())
            }
            is Event.OnDescriptionChange -> setState { copy(description = event.value) }
            is Event.OnStockUnitSelected -> setState { copy(stockUnitId = event.id) }
            is Event.OnPurchaseUnitSelected -> setState { copy(purchaseUnitId = event.id) }
            is Event.OnLocationSelected -> setState { copy(locationId = event.id) }
            is Event.OnProductGroupSelected -> setState { copy(productGroupId = event.id) }
            is Event.OnMinStockChange -> setState { copy(minStockAmount = event.value) }
            is Event.OnSubmit -> submit()
        }
    }

    private suspend fun loadOptions() {
        setState { copy(isLoadingOptions = true, optionsError = false) }
        val result = runCatching {
            coroutineScope {
                val units = async { getQuantityUnitsUseCase().getOrThrow() }
                val locations = async { getLocationsUseCase().getOrThrow() }
                val groups = async { getProductGroupsUseCase().getOrThrow() }
                Triple(units.await(), locations.await(), groups.await())
            }
        }
        result.onSuccess { (units, locations, groups) ->
            setState {
                copy(
                    isLoadingOptions = false,
                    optionsError = false,
                    quantityUnits = units.map { it.toOption() },
                    locations = locations.map { it.toOption() },
                    productGroups = groups.map { it.toOption() },
                )
            }
        }.onFailure {
            setState { copy(isLoadingOptions = false, optionsError = true) }
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
                quIdStock = current.stockUnitId!!,
                quIdPurchase = current.purchaseUnitId!!,
                locationId = current.locationId!!,
                description = current.description.ifBlank { null },
                minStockAmount = current.minStockAmount.toDoubleOrNull(),
                productGroupId = current.productGroupId,
            )
        )
        setState { copy(isSubmitting = false) }
        result
            .onSuccess { launchEffect(Effect.ProductCreated(current.name.trim())) }
            .onFailure { launchEffect(Effect.ShowError("No se pudo crear el producto")) }
    }
}
