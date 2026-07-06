package com.srfmolina.krocy.ui.presentation.feature.creation

import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuConversionsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuantityUnitsUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateProductUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateQuConversionUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.common.mapper.toOption
import com.srfmolina.krocy.ui.presentation.common.mapper.toUi
import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.QuConversionUi
import com.srfmolina.krocy.ui.presentation.common.model.toOptionsUi
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.State
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex

internal class CreateProductViewModel(
    private val createProductUseCase: CreateProductUseCase,
    private val createQuConversionUseCase: CreateQuConversionUseCase,
    private val getQuantityUnitsUseCase: GetQuantityUnitsUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getProductGroupsUseCase: GetProductGroupsUseCase,
    private val getQuConversionsUseCase: GetQuConversionsUseCase,
) : BaseViewModel<Event, State, Effect>() {

    private val submitLock = Mutex()

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
        data class OnConversionFactorChange(val value: String) : Event
        data class OnBestBeforeDaysChange(val value: String) : Event
        data class OnBestBeforeDaysAfterOpenChange(val value: String) : Event
        data class OnShouldNotBeFrozenChange(val value: Boolean) : Event
        data object OnToggleAdvanced : Event
        data class OnSubmit(val form: State) : Event
    }

    sealed interface Effect : UiEffect {
        data class ProductCreated(val name: String, val conversionWarning: Boolean) : Effect
        data class ShowError(val message: String) : Effect
    }

    data class State(
        val quantityUnits: OptionsUi = OptionsUi(),
        val locations: OptionsUi = OptionsUi(),
        val productGroups: OptionsUi = OptionsUi(),
        val conversions: List<QuConversionUi> = emptyList(),
        val conversionsLoading: Boolean = true,
        val conversionsError: Boolean = false,
        val name: String = "",
        val description: String = "",
        val stockUnitId: Int? = null,
        val purchaseUnitId: Int? = null,
        val purchaseUnitTouched: Boolean = false,
        val locationId: Int? = null,
        val productGroupId: Int? = null,
        val minStockAmount: String = "",
        val conversionFactor: String = "",
        val defaultBestBeforeDays: String = "",
        val defaultBestBeforeDaysAfterOpen: String = "",
        val shouldNotBeFrozen: Boolean = false,
        val advancedExpanded: Boolean = false,
        val showNameError: Boolean = false,
        val isSubmitting: Boolean = false,
    ) : UiState {
        val isLoadingOptions: Boolean
            get() = quantityUnits.isLoading || locations.isLoading ||
                productGroups.isLoading || conversionsLoading

        val optionsError: Boolean
            get() = quantityUnits.isError || locations.isError ||
                productGroups.isError || conversionsError

        val unitsDiffer: Boolean
            get() = stockUnitId != null && purchaseUnitId != null && stockUnitId != purchaseUnitId

        /** Factor of an existing global conversion purchase→stock, inverting reverse matches. */
        val existingConversionFactor: Double?
            get() {
                if (!unitsDiffer) return null
                conversions.firstOrNull { it.fromQuId == purchaseUnitId && it.toQuId == stockUnitId }
                    ?.let { return it.factor }
                return conversions
                    .firstOrNull { it.fromQuId == stockUnitId && it.toQuId == purchaseUnitId }
                    ?.let { 1.0 / it.factor }
            }

        val needsConversionFactor: Boolean
            get() = unitsDiffer && existingConversionFactor == null

        val conversionFactorValid: Boolean
            get() = !needsConversionFactor ||
                (conversionFactor.toDoubleOrNull()?.let { it > 0.0 } == true)

        val stockUnitName: String?
            get() = quantityUnits.options.firstOrNull { it.id == stockUnitId }?.label

        val purchaseUnitName: String?
            get() = quantityUnits.options.firstOrNull { it.id == purchaseUnitId }?.label

        val minStockValid: Boolean
            get() = minStockAmount.isBlank() || minStockAmount.toDoubleOrNull() != null

        val bestBeforeDaysValid: Boolean
            get() = defaultBestBeforeDays.isBlank() ||
                (defaultBestBeforeDays.toIntOrNull()?.let { it >= 0 } == true)

        val bestBeforeDaysAfterOpenValid: Boolean
            get() = defaultBestBeforeDaysAfterOpen.isBlank() ||
                (defaultBestBeforeDaysAfterOpen.toIntOrNull()?.let { it >= 0 } == true)

        val missingFields: List<String>
            get() = buildList {
                if (name.isBlank()) add("Nombre")
                if (stockUnitId == null) add("Unidad de stock")
                if (purchaseUnitId == null) add("Unidad de compra")
                if (locationId == null) add("Ubicación")
                if (!conversionFactorValid) add("Factor de conversión")
                if (!minStockValid) add("Stock mínimo")
                if (!bestBeforeDaysValid) add("Caducidad por defecto")
                if (!bestBeforeDaysAfterOpenValid) add("Caducidad tras abrir")
            }

        val isValid: Boolean get() = missingFields.isEmpty()
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
            is Event.OnStockUnitSelected -> setState {
                copy(
                    stockUnitId = event.id,
                    purchaseUnitId = if (purchaseUnitTouched) purchaseUnitId else event.id,
                )
            }
            is Event.OnPurchaseUnitSelected -> setState {
                copy(purchaseUnitId = event.id, purchaseUnitTouched = true)
            }
            is Event.OnLocationSelected -> setState { copy(locationId = event.id) }
            is Event.OnProductGroupSelected -> setState { copy(productGroupId = event.id) }
            is Event.OnMinStockChange -> setState { copy(minStockAmount = event.value) }
            is Event.OnConversionFactorChange -> setState { copy(conversionFactor = event.value) }
            is Event.OnBestBeforeDaysChange -> setState { copy(defaultBestBeforeDays = event.value) }
            is Event.OnBestBeforeDaysAfterOpenChange -> setState {
                copy(defaultBestBeforeDaysAfterOpen = event.value)
            }
            is Event.OnShouldNotBeFrozenChange -> setState { copy(shouldNotBeFrozen = event.value) }
            is Event.OnToggleAdvanced -> setState { copy(advancedExpanded = !advancedExpanded) }
            is Event.OnSubmit -> submit(event.form)
        }
    }

    private suspend fun loadOptions() {
        setState {
            copy(
                quantityUnits = OptionsUi(),
                locations = OptionsUi(),
                productGroups = OptionsUi(),
                conversions = emptyList(),
                conversionsLoading = true,
                conversionsError = false,
            )
        }
        coroutineScope {
            val units = async { getQuantityUnitsUseCase().toOptionsUi { it.toOption() } }
            val locations = async { getLocationsUseCase().toOptionsUi { it.toOption() } }
            val groups = async { getProductGroupsUseCase().toOptionsUi { it.toOption() } }
            val conversions = async { getQuConversionsUseCase() }

            val loadedUnits = units.await()
            val loadedLocations = locations.await()
            val loadedGroups = groups.await()
            val loadedConversions = conversions.await()
            setState {
                copy(
                    quantityUnits = loadedUnits,
                    locations = loadedLocations,
                    productGroups = loadedGroups,
                    conversions = loadedConversions.getOrDefault(emptyList()).map { it.toUi() },
                    conversionsLoading = false,
                    conversionsError = loadedConversions.isFailure,
                )
            }
        }
    }

    private suspend fun submit(form: State) {
        if (!form.isValid) {
            setState { copy(showNameError = form.name.isBlank()) }
            return
        }

        // Events are not serialized (each runs in its own coroutine), so a second OnSubmit
        // dispatched before recomposition disables the button must be rejected here.
        if (!submitLock.tryLock()) return
        try {
            setState { copy(isSubmitting = true) }
            val productId = createProductUseCase(
                NewProduct(
                    name = form.name.trim(),
                    quIdStock = form.stockUnitId!!,
                    quIdPurchase = form.purchaseUnitId!!,
                    locationId = form.locationId!!,
                    description = form.description.ifBlank { null },
                    minStockAmount = form.minStockAmount.toDoubleOrNull(),
                    productGroupId = form.productGroupId,
                    defaultBestBeforeDays = form.defaultBestBeforeDays.toIntOrNull(),
                    defaultBestBeforeDaysAfterOpen = form.defaultBestBeforeDaysAfterOpen.toIntOrNull(),
                    shouldNotBeFrozen = form.shouldNotBeFrozen,
                )
            ).getOrNull()

            if (productId == null) {
                launchEffect(Effect.ShowError("No se pudo crear el producto"))
                return
            }

            var conversionWarning = false
            if (form.needsConversionFactor) {
                val result = createQuConversionUseCase(
                    NewQuConversion(
                        productId = productId,
                        fromQuId = form.purchaseUnitId,
                        toQuId = form.stockUnitId,
                        factor = form.conversionFactor.toDouble(),
                    )
                )
                conversionWarning = result.isFailure
            }

            launchEffect(Effect.ProductCreated(form.name.trim(), conversionWarning))
        } finally {
            setState { copy(isSubmitting = false) }
            submitLock.unlock()
        }
    }
}
