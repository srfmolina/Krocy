package com.srfmolina.krocy.ui.presentation.feature.purchase

import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.model.masterdata.QuConversion
import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit
import com.srfmolina.krocy.domain.model.masterdata.ShoppingLocation
import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.model.product.ProductOption
import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo
import com.srfmolina.krocy.domain.model.stock.NewPurchase
import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.repository.StockRepository
import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetShoppingLocationsUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductPurchaseInfoUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductsUseCase
import com.srfmolina.krocy.domain.usecase.stock.PurchaseStockUseCase
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class PurchaseViewModelTest {

    private class MasterRepositoryStub : MasterRepository {
        override suspend fun getQuantityUnits() = emptyList<QuantityUnit>()
        override suspend fun getLocations() = listOf(Location(id = 11, name = "Nevera"))
        override suspend fun getProductGroups() = emptyList<ProductGroup>()
        override suspend fun getQuConversions() = emptyList<QuConversion>()
        override suspend fun getShoppingLocations() = listOf(ShoppingLocation(id = 5, name = "Mercadona"))
    }

    private class ProductRepositoryStub(
        var info: ProductPurchaseInfo,
        var throwOnGetProducts: Boolean = false,
        var throwOnGetPurchaseInfo: Boolean = false,
    ) : ProductRepository {
        override suspend fun createProduct(product: NewProduct): Int = 1
        override suspend fun createQuConversion(conversion: NewQuConversion): Int = 1
        override suspend fun getProducts(): List<ProductOption> {
            if (throwOnGetProducts) error("boom")
            return listOf(ProductOption(id = 7, name = "Leche"))
        }
        override suspend fun getProductPurchaseInfo(productId: Int): ProductPurchaseInfo {
            if (throwOnGetPurchaseInfo) error("boom")
            return info
        }
    }

    private class StockRepositoryFake : StockRepository {
        val purchases = mutableListOf<NewPurchase>()
        override fun getStock(): Flow<List<StockItem>> = flowOf(emptyList())
        override suspend fun consume(productId: Int, amount: Int) {}
        override suspend fun open(productId: Int, amount: Int) {}
        override suspend fun add(productId: Int, amount: Int) {}
        override suspend fun purchase(purchase: NewPurchase) {
            delay(100) // keep the request in flight so a second submit can race it
            purchases += purchase
        }
        override suspend fun forceRefresh() {}
    }

    private fun info(
        factor: Double = 6.0,
        defaultBestBeforeDays: Int? = 7,
        defaultLocationId: Int? = 11,
    ) = ProductPurchaseInfo(
        productId = 7,
        purchaseUnitName = "Paquete",
        purchaseUnitNamePlural = "Paquetes",
        stockUnitName = "Unidad",
        stockUnitNamePlural = "Unidades",
        conversionFactorPurchaseToStock = factor,
        defaultBestBeforeDays = defaultBestBeforeDays,
        defaultLocationId = defaultLocationId,
        lastPrice = 0.5,
        lastShoppingLocationId = 5,
    )

    private fun viewModel(
        stockRepo: StockRepository = StockRepositoryFake(),
        productRepo: ProductRepository = ProductRepositoryStub(info()),
    ) = PurchaseViewModel(
        purchaseStockUseCase = PurchaseStockUseCase(stockRepo),
        getProductsUseCase = GetProductsUseCase(productRepo),
        getProductPurchaseInfoUseCase = GetProductPurchaseInfoUseCase(productRepo),
        getLocationsUseCase = GetLocationsUseCase(MasterRepositoryStub()),
        getShoppingLocationsUseCase = GetShoppingLocationsUseCase(MasterRepositoryStub()),
    )

    private fun today() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads products, locations and stores`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel()

        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        assertEquals("Leche", vm.state.value.products.options.single().label)
        assertEquals("Nevera", vm.state.value.locations.options.single().label)
        assertEquals("Mercadona", vm.state.value.shoppingLocations.options.single().label)
        assertFalse(vm.state.value.isLoadingOptions)
    }

    @Test
    fun `selecting a product prefills due date and location from its defaults`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel()
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        assertEquals(today().plus(7, DateTimeUnit.DAY), vm.state.value.dueDate)
        assertFalse(vm.state.value.neverOverdue)
        assertEquals(11, vm.state.value.locationId)
        assertEquals("Paquete", vm.state.value.info?.purchaseUnitName)
    }

    @Test
    fun `a product that never expires activates never overdue`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(productRepo = ProductRepositoryStub(info(defaultBestBeforeDays = -1)))
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        assertTrue(vm.state.value.neverOverdue)
        assertNull(vm.state.value.dueDate)
    }

    @Test
    fun `a product without default due days leaves the date empty`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(productRepo = ProductRepositoryStub(info(defaultBestBeforeDays = 0)))
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        assertFalse(vm.state.value.neverOverdue)
        assertNull(vm.state.value.dueDate)
    }

    @Test
    fun `reselecting a product overwrites the previous prefills`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val productRepo = ProductRepositoryStub(info(defaultBestBeforeDays = 7, defaultLocationId = 11))
        val vm = viewModel(productRepo = productRepo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        productRepo.info = info(defaultBestBeforeDays = -1, defaultLocationId = 12)
        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        assertTrue(vm.state.value.neverOverdue)
        assertNull(vm.state.value.dueDate)
        assertEquals(12, vm.state.value.locationId)
    }

    @Test
    fun `submit converts amount and unit price from purchase to stock units`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val stockRepo = StockRepositoryFake()
        val vm = viewModel(stockRepo = stockRepo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        vm.launchEvent(Event.OnAmountChange("2"))
        vm.launchEvent(Event.OnPriceChange("3"))
        advanceUntilIdle()
        vm.launchEvent(Event.OnSubmit(vm.state.value))
        advanceUntilIdle()

        val purchase = stockRepo.purchases.single()
        assertEquals(12.0, purchase.amountStockUnits) // 2 packs x 6
        assertEquals(0.5, purchase.pricePerStockUnit) // 3 per pack / 6
        assertEquals(11, purchase.locationId)
        assertEquals(today().plus(7, DateTimeUnit.DAY), purchase.dueDate)
    }

    @Test
    fun `submit in stock units skips the conversion`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val stockRepo = StockRepositoryFake()
        val vm = viewModel(stockRepo = stockRepo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        vm.launchEvent(Event.OnAmountUnitChange(AmountUnitUi.STOCK))
        vm.launchEvent(Event.OnAmountChange("2"))
        vm.launchEvent(Event.OnPriceChange("3"))
        advanceUntilIdle()
        vm.launchEvent(Event.OnSubmit(vm.state.value))
        advanceUntilIdle()

        val purchase = stockRepo.purchases.single()
        assertEquals(2.0, purchase.amountStockUnits)
        assertEquals(3.0, purchase.pricePerStockUnit)
    }

    @Test
    fun `total price mode divides by the stock amount`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val stockRepo = StockRepositoryFake()
        val vm = viewModel(stockRepo = stockRepo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        vm.launchEvent(Event.OnAmountChange("2"))
        vm.launchEvent(Event.OnPriceModeChange(PriceModeUi.TOTAL))
        vm.launchEvent(Event.OnPriceChange("6"))
        advanceUntilIdle()
        vm.launchEvent(Event.OnSubmit(vm.state.value))
        advanceUntilIdle()

        assertEquals(0.5, stockRepo.purchases.single().pricePerStockUnit) // 6 / (2x6)
    }

    @Test
    fun `never overdue submits the far future date`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val stockRepo = StockRepositoryFake()
        val vm = viewModel(stockRepo = stockRepo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        vm.launchEvent(Event.OnNeverOverdueChange(true))
        advanceUntilIdle()
        vm.launchEvent(Event.OnSubmit(vm.state.value))
        advanceUntilIdle()

        assertEquals(LocalDate(2999, 12, 31), stockRepo.purchases.single().dueDate)
    }

    @Test
    fun `a second submit while one is in flight registers a single purchase`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val stockRepo = StockRepositoryFake()
        val vm = viewModel(stockRepo = stockRepo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        val form = vm.state.value
        vm.launchEvent(Event.OnSubmit(form))
        vm.launchEvent(Event.OnSubmit(form))
        advanceUntilIdle()

        assertEquals(1, stockRepo.purchases.size)
        assertFalse(vm.state.value.isSubmitting)
    }

    @Test
    fun `submit with an invalid form registers no purchase and emits no effect`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val stockRepo = StockRepositoryFake()
        val vm = viewModel(stockRepo = stockRepo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        val effects = mutableListOf<PurchaseViewModel.Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        // No product was selected, so the form is invalid (missing product and due date).
        vm.launchEvent(Event.OnSubmit(vm.state.value))
        advanceUntilIdle()

        assertTrue(stockRepo.purchases.isEmpty())
        assertTrue(effects.isEmpty())
        collector.cancel()
    }

    @Test
    fun `a failing products load surfaces an options error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(productRepo = ProductRepositoryStub(info(), throwOnGetProducts = true))

        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        assertTrue(vm.state.value.optionsError)
        assertFalse(vm.state.value.isLoadingOptions)
    }

    @Test
    fun `a failing purchase info fetch sets infoError`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(productRepo = ProductRepositoryStub(info(), throwOnGetPurchaseInfo = true))
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnProductSelected(7))
        advanceUntilIdle()

        assertTrue(vm.state.value.infoError)
        assertFalse(vm.state.value.infoLoading)
        assertNull(vm.state.value.info)
    }
}
