package com.srfmolina.krocy.ui.presentation.feature.stock

import com.srfmolina.krocy.domain.model.stock.NewPurchase
import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.repository.StockRepository
import com.srfmolina.krocy.domain.usecase.stock.AddStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ConsumeStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.LoadStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ObserveStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.OpenStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.RefreshStockUseCase
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    private val serverItems = listOf(
        StockItem(1, "Leche entera", emptyList(), consumptionDate = null, quantity = "3 briks", pictureUrl = null),
        StockItem(2, "Manzanas", emptyList(), consumptionDate = null, quantity = "4 uds", pictureUrl = null),
    )

    /**
     * Mirrors the repository contract: the stream emits nothing until a load or refresh
     * succeeds, and it never fails or completes, so one collector survives a failed load.
     */
    private class StockRepositoryFake(
        private val serverItems: List<StockItem>,
        var failLoad: Boolean = false,
    ) : StockRepository {
        val cache = MutableStateFlow<List<StockItem>?>(null)
        var loadCalls = 0

        override fun getStock(): Flow<List<StockItem>> = cache.filterNotNull()

        override suspend fun ensureLoaded() {
            loadCalls++
            if (cache.value != null) return
            if (failLoad) error("boom")
            cache.value = serverItems
        }

        override suspend fun forceRefresh() {
            if (failLoad) error("boom")
            cache.value = serverItems
        }

        override suspend fun consume(productId: Int, amount: Int) {}
        override suspend fun open(productId: Int, amount: Int) {}
        override suspend fun add(productId: Int, amount: Int) {}
        override suspend fun purchase(purchase: NewPurchase) {}
    }

    private fun viewModel(repo: StockRepository) = StockViewModel(
        observeStockUseCase = ObserveStockUseCase(repo),
        consumeStockUseCase = ConsumeStockUseCase(repo),
        addStockUseCase = AddStockUseCase(repo),
        openStockUseCase = OpenStockUseCase(repo),
        refreshStockUseCase = RefreshStockUseCase(repo),
        loadStockUseCase = LoadStockUseCase(repo),
    )

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads the stock and shows it`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = StockRepositoryFake(serverItems)
        val vm = viewModel(repo)

        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        assertEquals(1, repo.loadCalls)
        assertFalse(vm.state.value.isLoading)
        assertEquals(listOf("Leche entera", "Manzanas"), vm.state.value.items.map { it.name })
    }

    @Test
    fun `a failed first load is recovered by a refresh`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = StockRepositoryFake(serverItems, failLoad = true)
        val vm = viewModel(repo)
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        assertFalse(vm.state.value.isLoading)
        assertTrue(vm.state.value.items.isEmpty())

        repo.failLoad = false
        vm.launchEvent(Event.OnRefresh)
        advanceUntilIdle()

        assertEquals(2, vm.state.value.items.size)
        // Reached by the collector started in Init: nothing had to re-subscribe.
        assertEquals(1, repo.cache.subscriptionCount.value)
    }

    @Test
    fun `a second Init neither loads again nor starts a second collection`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = StockRepositoryFake(serverItems)
        val vm = viewModel(repo)

        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        // LaunchedEffect(Unit) re-sends Init whenever the screen re-enters composition while
        // the ViewModel survives.
        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        assertEquals(1, repo.loadCalls)
        assertEquals(1, repo.cache.subscriptionCount.value)
    }
}
