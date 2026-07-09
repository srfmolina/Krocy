package com.srfmolina.krocy.ui.presentation.feature.purchase

import com.srfmolina.krocy.domain.model.stock.NewPurchase
import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetShoppingLocationsUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductPurchaseInfoUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductsUseCase
import com.srfmolina.krocy.domain.usecase.stock.PurchaseStockUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.base.UiEffect
import com.srfmolina.krocy.ui.base.UiEvent
import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.common.mapper.toOption
import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.common.model.toOptionsUi
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel.State
import com.srfmolina.krocy.ui.presentation.feature.purchase.mapper.toUi
import com.srfmolina.krocy.ui.presentation.feature.purchase.model.ProductPurchaseInfoUi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.math.round
import kotlin.time.Clock

internal enum class AmountUnitUi { PURCHASE, STOCK }

internal enum class PriceModeUi { UNIT, TOTAL }

/** Grocy's conventional "never overdue" due date. */
internal val NEVER_OVERDUE_DATE = LocalDate(2999, 12, 31)

internal class PurchaseViewModel(
    private val purchaseStockUseCase: PurchaseStockUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductPurchaseInfoUseCase: GetProductPurchaseInfoUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getShoppingLocationsUseCase: GetShoppingLocationsUseCase,
) : BaseViewModel<Event, State, Effect>() {

    private val submitLock = Mutex()

    sealed interface Event : UiEvent {
        data object Init : Event
        data object OnRetryLoadOptions : Event
        data class OnProductSelected(val id: Int?) : Event
        data object OnRetryLoadInfo : Event
        data class OnAmountChange(val value: String) : Event
        data class OnAmountUnitChange(val value: AmountUnitUi) : Event
        data class OnDueDateChange(val value: LocalDate?) : Event
        data class OnNeverOverdueChange(val value: Boolean) : Event
        data class OnPriceChange(val value: String) : Event
        data class OnPriceModeChange(val value: PriceModeUi) : Event
        data class OnStoreSelected(val id: Int?) : Event
        data class OnLocationSelected(val id: Int?) : Event
        data class OnNoteChange(val value: String) : Event
        data object OnToggleAdvanced : Event
        data class OnSubmit(val form: State) : Event
    }

    sealed interface Effect : UiEffect {
        data class PurchaseRegistered(val productName: String) : Effect
        data class ShowError(val message: String) : Effect
    }

    data class State(
        val products: OptionsUi = OptionsUi(),
        val locations: OptionsUi = OptionsUi(),
        val shoppingLocations: OptionsUi = OptionsUi(),
        val selectedProductId: Int? = null,
        val info: ProductPurchaseInfoUi? = null,
        val infoLoading: Boolean = false,
        val infoError: Boolean = false,
        val amount: String = "1",
        val amountUnit: AmountUnitUi = AmountUnitUi.PURCHASE,
        val dueDate: LocalDate? = null,
        val neverOverdue: Boolean = false,
        val price: String = "",
        val priceMode: PriceModeUi = PriceModeUi.UNIT,
        val storeId: Int? = null,
        val locationId: Int? = null,
        val note: String = "",
        val advancedExpanded: Boolean = false,
        val isSubmitting: Boolean = false,
    ) : UiState {

        val isLoadingOptions: Boolean
            get() = products.isLoading || locations.isLoading || shoppingLocations.isLoading

        val optionsError: Boolean
            get() = products.isError || locations.isError || shoppingLocations.isError

        val selectedProductName: String?
            get() = products.options.firstOrNull { it.id == selectedProductId }?.label

        val amountValid: Boolean
            get() = amount.toDoubleOrNull()?.let { it > 0.0 } == true

        val priceValid: Boolean
            get() = price.isBlank() || (price.toDoubleOrNull()?.let { it >= 0.0 } == true)

        val dueDateValid: Boolean
            get() = neverOverdue || dueDate != null

        /** Factor from the selected amount unit to the stock unit. */
        val unitFactor: Double
            get() = if (amountUnit == AmountUnitUi.PURCHASE) {
                info?.conversionFactorPurchaseToStock ?: 1.0
            } else {
                1.0
            }

        val sameUnits: Boolean
            get() = info?.let { it.purchaseUnitName == it.stockUnitName } == true

        val amountUnitOptions: List<SelectableOptionUi>
            get() = info?.let {
                listOf(
                    SelectableOptionUi(AmountUnitUi.PURCHASE.ordinal, it.purchaseUnitName),
                    SelectableOptionUi(AmountUnitUi.STOCK.ordinal, it.stockUnitName),
                )
            }.orEmpty()

        val selectedUnitName: String?
            get() = info?.let {
                if (amountUnit == AmountUnitUi.PURCHASE) it.purchaseUnitName else it.stockUnitName
            }

        /** Last known price converted to the selected amount unit, as a form hint. */
        val lastPriceHint: String?
            get() {
                val current = info ?: return null
                val last = current.lastPrice ?: return null
                val perSelectedUnit = round(last * unitFactor * 100) / 100
                return "Último precio: $perSelectedUnit por ${selectedUnitName.orEmpty()}"
            }

        val missingFields: List<String>
            get() = buildList {
                if (selectedProductId == null) add("Producto")
                if (selectedProductId != null && info == null) add("Datos del producto")
                if (!amountValid) add("Cantidad")
                if (!dueDateValid) add("Fecha de caducidad")
                if (!priceValid) add("Precio")
            }

        val isValid: Boolean get() = missingFields.isEmpty()
    }

    override fun createInitialState(): State = State()

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> loadOptions()
            is Event.OnRetryLoadOptions -> loadOptions()
            is Event.OnProductSelected -> onProductSelected(event.id)
            is Event.OnRetryLoadInfo -> currentState.selectedProductId?.let { retryLoadInfo(it) }
            is Event.OnAmountChange -> setState { copy(amount = event.value) }
            is Event.OnAmountUnitChange -> setState { copy(amountUnit = event.value) }
            is Event.OnDueDateChange -> setState { copy(dueDate = event.value) }
            is Event.OnNeverOverdueChange -> setState {
                copy(neverOverdue = event.value, dueDate = if (event.value) null else dueDate)
            }
            is Event.OnPriceChange -> setState { copy(price = event.value) }
            is Event.OnPriceModeChange -> setState { copy(priceMode = event.value) }
            is Event.OnStoreSelected -> setState { copy(storeId = event.id) }
            is Event.OnLocationSelected -> setState { copy(locationId = event.id) }
            is Event.OnNoteChange -> setState { copy(note = event.value) }
            is Event.OnToggleAdvanced -> setState { copy(advancedExpanded = !advancedExpanded) }
            is Event.OnSubmit -> submit(event.form)
        }
    }

    private suspend fun loadOptions() {
        setState {
            copy(products = OptionsUi(), locations = OptionsUi(), shoppingLocations = OptionsUi())
        }
        coroutineScope {
            val products = async { getProductsUseCase().toOptionsUi { it.toOption() } }
            val locations = async { getLocationsUseCase().toOptionsUi { it.toOption() } }
            val stores = async { getShoppingLocationsUseCase().toOptionsUi { it.toOption() } }

            val loadedProducts = products.await()
            val loadedLocations = locations.await()
            val loadedStores = stores.await()
            setState {
                copy(
                    products = loadedProducts,
                    locations = loadedLocations,
                    shoppingLocations = loadedStores,
                )
            }
        }
    }

    private suspend fun onProductSelected(id: Int?) {
        if (id == null) {
            setState {
                copy(selectedProductId = null, info = null, infoLoading = false, infoError = false)
            }
            return
        }
        setState { copy(selectedProductId = id, info = null, infoLoading = true, infoError = false) }
        loadInfo(id)
    }

    private suspend fun retryLoadInfo(productId: Int) {
        setState { copy(infoLoading = true, infoError = false) }
        loadInfo(productId)
    }

    private suspend fun loadInfo(productId: Int) {
        getProductPurchaseInfoUseCase(productId).fold(
            onSuccess = { info ->
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val days = info.defaultBestBeforeDays
                setState {
                    // A newer selection may have raced this response; keep only the current one.
                    if (selectedProductId != productId) this
                    else copy(
                        info = info.toUi(),
                        infoLoading = false,
                        amountUnit = AmountUnitUi.PURCHASE,
                        dueDate = if (days != null && days > 0) today.plus(days, DateTimeUnit.DAY) else null,
                        neverOverdue = days == -1,
                        locationId = info.defaultLocationId,
                    )
                }
            },
            onFailure = {
                setState {
                    if (selectedProductId != productId) this
                    else copy(infoLoading = false, infoError = true)
                }
            },
        )
    }

    private suspend fun submit(form: State) {
        if (!form.isValid) return
        val info = form.info ?: return
        val productName = form.selectedProductName ?: return

        // Events are not serialized (each runs in its own coroutine), so a second OnSubmit
        // dispatched before recomposition disables the button must be rejected here.
        if (!submitLock.tryLock()) return
        try {
            setState { copy(isSubmitting = true) }

            val factor = if (form.amountUnit == AmountUnitUi.PURCHASE) {
                info.conversionFactorPurchaseToStock
            } else {
                1.0
            }
            val amountStockUnits = form.amount.toDouble() * factor
            val pricePerStockUnit = form.price.toDoubleOrNull()?.let { raw ->
                when (form.priceMode) {
                    PriceModeUi.UNIT -> raw / factor
                    PriceModeUi.TOTAL -> raw / amountStockUnits
                }
            }

            purchaseStockUseCase(
                NewPurchase(
                    productId = form.selectedProductId!!,
                    amountStockUnits = amountStockUnits,
                    dueDate = if (form.neverOverdue) NEVER_OVERDUE_DATE else form.dueDate,
                    pricePerStockUnit = pricePerStockUnit,
                    locationId = form.locationId,
                    shoppingLocationId = form.storeId,
                    note = form.note.ifBlank { null },
                )
            ).fold(
                onSuccess = { launchEffect(Effect.PurchaseRegistered(productName)) },
                onFailure = { launchEffect(Effect.ShowError("No se pudo registrar la compra")) },
            )
        } finally {
            setState { copy(isSubmitting = false) }
            submitLock.unlock()
        }
    }
}
