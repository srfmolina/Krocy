package com.srfmolina.krocy.ui

import com.srfmolina.krocy.domain.model.example.KrocyItem
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.login.GetServerConfigUseCase
import com.srfmolina.krocy.domain.usecase.login.LogoutUseCase
import com.srfmolina.krocy.domain.usecase.login.ObserveSessionExpiredUseCase
import com.srfmolina.krocy.domain.usecase.login.OpenSessionUseCase
import com.srfmolina.krocy.ui.AppViewModel.Effect
import com.srfmolina.krocy.ui.AppViewModel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private class ServerConfigRepositoryFake(initial: ServerConfig? = null) : ServerConfigRepository {
        var stored: ServerConfig? = initial
        override val config: Flow<ServerConfig?> = MutableStateFlow(stored)
        override suspend fun get(): ServerConfig? = stored
        override suspend fun save(config: ServerConfig) { stored = config }
        override suspend fun clear() { stored = null }
    }

    private class SessionManagerFake(private val openSucceeds: Boolean = true) : SessionManager {
        var openCallCount = 0
        var closeCallCount = 0
        private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        override val sessionExpired: Flow<Unit> = _sessionExpired
        override suspend fun open(config: ServerConfig) {
            openCallCount++
            if (!openSucceeds) error("boom")
        }
        override suspend fun close() { closeCallCount++ }
        suspend fun expireSession() { _sessionExpired.emit(Unit) }
    }

    private class KrocyItemRepositoryFake : KrocyItemRepository {
        var clearAllCallCount = 0
        override fun getAll(): Flow<List<KrocyItem>> = emptyFlow()
        override suspend fun save(item: KrocyItem) = Unit
        override suspend fun deleteById(id: Int) = Unit
        override suspend fun clearAll() { clearAllCallCount++ }
    }

    private fun viewModel(
        configRepo: ServerConfigRepository,
        sessionManager: SessionManager,
        itemRepo: KrocyItemRepository
    ) = AppViewModel(
        getServerConfig = GetServerConfigUseCase(configRepo),
        openSession = OpenSessionUseCase(sessionManager),
        logout = LogoutUseCase(sessionManager, configRepo, itemRepo),
        observeSessionExpired = ObserveSessionExpiredUseCase(sessionManager)
    )

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init with a stored config that opens routes to stock`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(
            configRepo = ServerConfigRepositoryFake(initial = ServerConfig.Demo),
            sessionManager = SessionManagerFake(),
            itemRepo = KrocyItemRepositoryFake()
        )
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        assertEquals(listOf<Effect>(Effect.NavigateToStock), effects)
        assertFalse(vm.state.value.isLoading)
        collector.cancel()
    }

    @Test
    fun `init with no stored config routes to welcome`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(
            configRepo = ServerConfigRepositoryFake(initial = null),
            sessionManager = SessionManagerFake(),
            itemRepo = KrocyItemRepositoryFake()
        )
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.Init)
        advanceUntilIdle()

        assertEquals(listOf<Effect>(Effect.NavigateToWelcome), effects)
        assertFalse(vm.state.value.isLoading)
        collector.cancel()
    }

    @Test
    fun `a second Init after a rotation is a no-op`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val sessionManager = SessionManagerFake()
        val vm = viewModel(
            configRepo = ServerConfigRepositoryFake(initial = ServerConfig.Demo),
            sessionManager = sessionManager,
            itemRepo = KrocyItemRepositoryFake()
        )
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        vm.launchEvent(Event.Init) // simulates a config-change re-fire from LaunchedEffect(Unit)
        advanceUntilIdle()

        assertEquals(listOf<Effect>(Effect.NavigateToStock), effects)
        assertEquals(1, sessionManager.openCallCount)
        collector.cancel()
    }

    @Test
    fun `OnLogoutConfirm logs out and navigates to login`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val configRepo = ServerConfigRepositoryFake(initial = ServerConfig.Demo)
        val sessionManager = SessionManagerFake()
        val itemRepo = KrocyItemRepositoryFake()
        val vm = viewModel(configRepo, sessionManager, itemRepo)
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        effects.clear() // drop the startup NavigateToStock, only the logout effect matters here

        vm.launchEvent(Event.OnLogoutConfirm)
        advanceUntilIdle()

        assertEquals(1, sessionManager.closeCallCount)
        assertNull(configRepo.stored)
        assertEquals(1, itemRepo.clearAllCallCount)
        assertNull(vm.state.value.dialogConfig)
        assertNull(vm.state.value.topBarConfig)
        assertNull(vm.state.value.fabConfig)
        assertEquals(listOf<Effect>(Effect.NavigateToLogin), effects)
        collector.cancel()
    }

    @Test
    fun `a session expiry logs out and navigates to login`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val configRepo = ServerConfigRepositoryFake(initial = ServerConfig.Demo)
        val sessionManager = SessionManagerFake()
        val itemRepo = KrocyItemRepositoryFake()
        val vm = viewModel(configRepo, sessionManager, itemRepo)
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }
        vm.launchEvent(Event.Init)
        advanceUntilIdle()
        effects.clear() // drop the startup NavigateToStock, only the expiry effect matters here

        sessionManager.expireSession()
        advanceUntilIdle()

        assertEquals(1, sessionManager.closeCallCount)
        assertNull(configRepo.stored)
        assertEquals(1, itemRepo.clearAllCallCount)
        assertNull(vm.state.value.dialogConfig)
        assertNull(vm.state.value.topBarConfig)
        assertNull(vm.state.value.fabConfig)
        assertEquals(listOf<Effect>(Effect.NavigateToLogin), effects)
        collector.cancel()
    }
}
