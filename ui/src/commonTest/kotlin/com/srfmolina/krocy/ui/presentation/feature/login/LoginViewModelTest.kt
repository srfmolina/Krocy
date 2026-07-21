package com.srfmolina.krocy.ui.presentation.feature.login

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.login.CompleteLoginUseCase
import com.srfmolina.krocy.ui.presentation.feature.login.LoginViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.login.LoginViewModel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private class ServerConfigRepositoryFake : ServerConfigRepository {
        var stored: ServerConfig? = null
        override val config: Flow<ServerConfig?> = MutableSharedFlow()
        override suspend fun get(): ServerConfig? = stored
        override suspend fun save(config: ServerConfig) { stored = config }
        override suspend fun clear() { stored = null }
    }

    private class SessionManagerFake(private val openSucceeds: Boolean = true) : SessionManager {
        var openCallCount = 0
        override val sessionExpired: Flow<Unit> = MutableSharedFlow()
        override suspend fun open(config: ServerConfig) {
            openCallCount++
            if (!openSucceeds) error("boom")
        }
        override suspend fun close() = Unit
    }

    private fun viewModel(
        configRepo: ServerConfigRepository,
        sessionManager: SessionManager
    ) = LoginViewModel(
        completeLogin = CompleteLoginUseCase(configRepo, sessionManager)
    )

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OnDemoServerClick persists the demo config and navigates to stock`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val configRepo = ServerConfigRepositoryFake()
        val sessionManager = SessionManagerFake()
        val vm = viewModel(configRepo, sessionManager)
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.OnDemoServerClick)
        advanceUntilIdle()

        assertEquals(ServerConfig.Demo, configRepo.stored)
        assertEquals(1, sessionManager.openCallCount)
        assertEquals(listOf<Effect>(Effect.NavigateToStock), effects)
        assertFalse(vm.state.value.isConnecting)
        collector.cancel()
    }

    @Test
    fun `OnDemoServerClick shows an error when the session fails to open`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val configRepo = ServerConfigRepositoryFake()
        val sessionManager = SessionManagerFake(openSucceeds = false)
        val vm = viewModel(configRepo, sessionManager)
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.OnDemoServerClick)
        advanceUntilIdle()

        assertEquals(
            listOf<Effect>(Effect.ShowError("No se pudo preparar el servidor de prueba")),
            effects
        )
        assertFalse(vm.state.value.isConnecting)
        collector.cancel()
    }

    @Test
    fun `OnOwnServerClick navigates to server setup`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(ServerConfigRepositoryFake(), SessionManagerFake())
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.launchEvent(Event.OnOwnServerClick)
        advanceUntilIdle()

        assertEquals(listOf<Effect>(Effect.NavigateToServerSetup), effects)
        collector.cancel()
    }
}
