package com.srfmolina.krocy.ui.presentation.feature.shoppinglist

import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.model.product.ProductOption
import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo
import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.usecase.product.GetProductsUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.ObserveShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.RefreshShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.SetEntryDoneUseCase
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingListEntryUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModelTest {

    private val defaultEntries = listOf(
        ShoppingListEntry(10, 1, "Leche entera", "Lácteos", 3.0, "brik", "briks", done = false),
        ShoppingListEntry(11, 2, "Manzanas", "Frutas y verduras", 4.0, "ud", "uds", done = false),
        ShoppingListEntry(12, 3, "Pan de molde", null, 1.0, "paquete", "paquetes", done = false),
    )

    private class ShoppingListRepositoryFake(
        entries: List<ShoppingListEntry>,
        var failObserve: Boolean = false,
        var failSetDone: Boolean = false,
        var failAdd: Boolean = false,
        var failRefresh: Boolean = false,
    ) : ShoppingListRepository {
        val entriesFlow = MutableStateFlow(entries)
        val doneCalls = mutableListOf<Pair<Int, Boolean>>()
        val added = mutableListOf<Pair<Int, Double>>()
        var refreshCalls = 0

        override fun getShoppingList(): Flow<List<ShoppingListEntry>> =
            if (failObserve) flow { error("boom") } else entriesFlow

        override suspend fun setDone(entryId: Int, done: Boolean) {
            if (failSetDone) error("boom")
            doneCalls += entryId to done
        }

        override suspend fun addProduct(productId: Int, amount: Double) {
            delay(100) // keep the request in flight so a second submit can race it
            if (failAdd) error("boom")
            added += productId to amount
            // Mirror production: the repository's cache emits the newly added entry before
            // addProduct returns, which is what actually ends the ViewModel's loading state.
            entriesFlow.value = entriesFlow.value +
                ShoppingListEntry(99, productId, "Queso curado", null, amount, "ud", "uds", done = false)
        }

        override suspend fun forceRefresh() {
            refreshCalls++
            if (failRefresh) error("boom")
        }
    }

    private class ProductRepositoryStub(
        var throwOnGetProducts: Boolean = false,
    ) : ProductRepository {
        override suspend fun createProduct(product: NewProduct): Int = 1
        override suspend fun createQuConversion(conversion: NewQuConversion): Int = 1
        override suspend fun getProducts(): List<ProductOption> {
            if (throwOnGetProducts) error("boom")
            return listOf(ProductOption(id = 7, name = "Queso curado"))
        }
        override suspend fun getProductPurchaseInfo(productId: Int): ProductPurchaseInfo =
            error("unused")
    }

    private fun viewModel(
        repo: ShoppingListRepository = ShoppingListRepositoryFake(defaultEntries),
        productRepo: ProductRepository = ProductRepositoryStub(),
    ) = ShoppingListViewModel(
        observeShoppingListUseCase = ObserveShoppingListUseCase(repo),
        refreshShoppingListUseCase = RefreshShoppingListUseCase(repo),
        setEntryDoneUseCase = SetEntryDoneUseCase(repo),
        addToShoppingListUseCase = AddToShoppingListUseCase(repo),
        getProductsUseCase = GetProductsUseCase(productRepo),
    )

    private fun ShoppingListViewModel.entry(id: Int): ShoppingListEntryUi? =
        state.value.groups.flatMap { it.entries }.firstOrNull { it.id == id }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init groups entries alphabetically with the ungrouped section last`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel()

        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertEquals(
            listOf("Frutas y verduras", "Lácteos", "Otros"),
            vm.state.value.groups.map { it.displayName },
        )
        // Plural unit for amounts above one, singular for exactly one.
        assertEquals("3 briks", vm.entry(10)?.quantity)
        assertEquals("1 paquete", vm.entry(12)?.quantity)
    }

    @Test
    fun `toggling an entry flips it optimistically and persists the change`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnToggleDone(10))
        advanceUntilIdle()

        assertTrue(vm.entry(10)?.done == true)
        assertEquals(listOf(10 to true), repo.doneCalls)
    }

    @Test
    fun `a failing toggle reverts the flip and surfaces an error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries, failSetDone = true)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.OnToggleDone(10))
        advanceUntilIdle()

        assertTrue(vm.entry(10)?.done == false)
        assertTrue(effects.single() is Effect.ShowError)
        collector.cancel()
    }

    @Test
    fun `a failing load surfaces the error state and refresh recovers from it`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries, failObserve = true)
        val vm = viewModel(repo)

        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        assertTrue(vm.state.value.loadError)

        repo.failObserve = false
        vm.launchEvent(Event.OnRefresh)
        advanceUntilIdle()

        assertEquals(1, repo.refreshCalls)
        assertFalse(vm.state.value.loadError)
        assertEquals(3, vm.state.value.groups.size)
    }

    @Test
    fun `a failing refresh keeps the error state`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries, failRefresh = true)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnRefresh)
        advanceUntilIdle()

        assertTrue(vm.state.value.loadError)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `opening the add dialog loads the product options`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel()
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnOpenAddDialog)
        advanceUntilIdle()

        val dialog = assertNotNull(vm.state.value.addDialog)
        assertEquals("Queso curado", dialog.products.options.single().label)
        assertFalse(dialog.isValid) // no product selected yet
    }

    @Test
    fun `a failing products load marks the dialog options as errored`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(productRepo = ProductRepositoryStub(throwOnGetProducts = true))
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnOpenAddDialog)
        advanceUntilIdle()

        assertTrue(assertNotNull(vm.state.value.addDialog).products.isError)
    }

    @Test
    fun `submitting dismisses the dialog immediately and shows loading until the add completes`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.OnOpenAddDialog)
        advanceUntilIdle()
        vm.launchEvent(Event.OnAddProductSelected(7))
        vm.launchEvent(Event.OnAddAmountChange("2"))
        advanceUntilIdle()
        vm.launchEvent(Event.OnAddSubmit)
        runCurrent() // request still in flight: the fake's addProduct is parked at delay(100)

        assertNull(vm.state.value.addDialog)
        assertTrue(vm.state.value.isLoading)

        advanceUntilIdle()

        assertEquals(listOf(7 to 2.0), repo.added)
        assertFalse(vm.state.value.isLoading)
        assertEquals("Queso curado", (effects.single() as Effect.ProductAdded).productName)
        // The loading ended via the repository's cache emission delivering the new entry,
        // not just the defensive fallback in the ViewModel's success branch.
        assertEquals("Queso curado", vm.entry(99)?.name)
        collector.cancel()
    }

    @Test
    fun `a failing add stops the loading, keeps the dialog closed and surfaces an error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries, failAdd = true)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.OnOpenAddDialog)
        advanceUntilIdle()
        vm.launchEvent(Event.OnAddProductSelected(7))
        advanceUntilIdle()
        vm.launchEvent(Event.OnAddSubmit)
        advanceUntilIdle()

        assertNull(vm.state.value.addDialog)
        assertFalse(vm.state.value.isLoading)
        assertTrue(effects.single() is Effect.ShowError)
        collector.cancel()
    }

    @Test
    fun `a second submit while one is in flight adds a single product`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnOpenAddDialog)
        advanceUntilIdle()
        vm.launchEvent(Event.OnAddProductSelected(7))
        advanceUntilIdle()
        // The guard is submitAdd's null check: the first submit nulls the dialog before
        // any suspension point, so the second one returns early. No lock is involved.
        vm.launchEvent(Event.OnAddSubmit)
        vm.launchEvent(Event.OnAddSubmit)
        advanceUntilIdle()

        assertEquals(1, repo.added.size)
    }

    @Test
    fun `submitting an invalid dialog does nothing`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ShoppingListRepositoryFake(defaultEntries)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        vm.launchEvent(Event.OnOpenAddDialog)
        advanceUntilIdle()
        vm.launchEvent(Event.OnAddAmountChange("0"))
        advanceUntilIdle()
        vm.launchEvent(Event.OnAddSubmit)
        advanceUntilIdle()

        assertTrue(repo.added.isEmpty())
        assertNotNull(vm.state.value.addDialog)
    }
}
