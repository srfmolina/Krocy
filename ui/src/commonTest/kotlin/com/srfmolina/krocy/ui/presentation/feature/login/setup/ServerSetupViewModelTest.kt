package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.model.server.ServerValidation
import com.srfmolina.krocy.domain.repository.LoginRepository
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.login.CompleteLoginUseCase
import com.srfmolina.krocy.domain.usecase.login.ValidateServerUseCase
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.ConnectionUi
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.login.setup.ServerSetupViewModel.Event
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ServerSetupViewModelTest {

    private class LoginRepositoryFake(
        private val onValidate: suspend (ServerConfig) -> ServerValidation
    ) : LoginRepository {
        var callCount = 0
        override suspend fun validate(config: ServerConfig): ServerValidation {
            callCount++
            return onValidate(config)
        }
    }

    private class ServerConfigRepositoryFake : ServerConfigRepository {
        var stored: ServerConfig? = null
        override val config: Flow<ServerConfig?> = MutableSharedFlow()
        override suspend fun get(): ServerConfig? = stored
        override suspend fun save(config: ServerConfig) { stored = config }
        override suspend fun clear() { stored = null }
    }

    private class SessionManagerFake : SessionManager {
        var openCallCount = 0
        override val sessionExpired: Flow<Unit> = MutableSharedFlow()
        override suspend fun open(config: ServerConfig) { openCallCount++ }
        override suspend fun close() = Unit
    }

    private fun viewModel(
        loginRepo: LoginRepository,
        configRepo: ServerConfigRepository = ServerConfigRepositoryFake(),
        sessionManager: SessionManager = SessionManagerFake()
    ) = ServerSetupViewModel(
        validateServer = ValidateServerUseCase(loginRepo),
        completeLogin = CompleteLoginUseCase(configRepo, sessionManager)
    )

    private val validSelfHostedForm = ServerSetupForm(
        serverUrl = "https://grocy.example.com",
        apiKey = "key123"
    )

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OnConnectClick with an empty form reports field errors and never calls validateServer`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginRepo = LoginRepositoryFake { error("must not be called") }
        val vm = viewModel(loginRepo)

        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()

        assertEquals(
            ValidationResult.Invalid(serverUrlError = "Campo obligatorio", apiKeyError = "Campo obligatorio"),
            vm.state.value.fieldErrors
        )
        assertEquals(ConnectionUi.Idle, vm.state.value.connection)
        assertEquals(0, loginRepo.callCount)
    }

    @Test
    fun `OnConnectClick when validation fails maps the failure to an Error connection`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginRepo = LoginRepositoryFake { throw LoginFailure.NotGrocyInstance("HTTP 404") }
        val vm = viewModel(loginRepo)
        vm.launchEvent(Event.OnServerUrlChange(validSelfHostedForm.serverUrl))
        vm.launchEvent(Event.OnApiKeyChange(validSelfHostedForm.apiKey))

        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()

        assertEquals(
            ConnectionUi.Error(
                message = "No parece una instancia de Grocy. Si usas Home Assistant, " +
                    "activa el modo Home Assistant.",
                detail = "HTTP 404"
            ),
            vm.state.value.connection
        )
        assertNull(vm.state.value.fieldErrors)
    }

    @Test
    fun `OnConnectClick with an unsupported grocy version shows a VersionWarning without logging in`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginRepo = LoginRepositoryFake { ServerValidation.forVersion("3.3.1") }
        val sessionManager = SessionManagerFake()
        val vm = viewModel(loginRepo, sessionManager = sessionManager)
        vm.launchEvent(Event.OnServerUrlChange(validSelfHostedForm.serverUrl))
        vm.launchEvent(Event.OnApiKeyChange(validSelfHostedForm.apiKey))

        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()

        assertEquals(ConnectionUi.VersionWarning("3.3.1"), vm.state.value.connection)
        assertEquals(0, sessionManager.openCallCount)
    }

    @Test
    fun `OnContinueAnyway after a VersionWarning finishes login and navigates to stock`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginRepo = LoginRepositoryFake { ServerValidation.forVersion("3.3.1") }
        val configRepo = ServerConfigRepositoryFake()
        val sessionManager = SessionManagerFake()
        val vm = viewModel(loginRepo, configRepo, sessionManager)
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }
        vm.launchEvent(Event.OnServerUrlChange(validSelfHostedForm.serverUrl))
        vm.launchEvent(Event.OnApiKeyChange(validSelfHostedForm.apiKey))
        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()

        vm.launchEvent(Event.OnContinueAnyway)
        advanceUntilIdle()

        assertEquals(
            ServerConfig.SelfHosted(validSelfHostedForm.serverUrl, validSelfHostedForm.apiKey),
            configRepo.stored
        )
        assertEquals(1, sessionManager.openCallCount)
        assertEquals(listOf<Effect>(Effect.NavigateToStock), effects)
        collector.cancel()
    }

    @Test
    fun `OnDismissWarning resets the connection back to idle`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginRepo = LoginRepositoryFake { ServerValidation.forVersion("3.3.1") }
        val vm = viewModel(loginRepo)
        vm.launchEvent(Event.OnServerUrlChange(validSelfHostedForm.serverUrl))
        vm.launchEvent(Event.OnApiKeyChange(validSelfHostedForm.apiKey))
        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()
        assertEquals(ConnectionUi.VersionWarning("3.3.1"), vm.state.value.connection)

        vm.launchEvent(Event.OnDismissWarning)
        advanceUntilIdle()

        assertEquals(ConnectionUi.Idle, vm.state.value.connection)
    }

    @Test
    fun `OnConnectClick with a supported version finishes login directly and navigates to stock`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginRepo = LoginRepositoryFake { ServerValidation.forVersion("4.2.0") }
        val configRepo = ServerConfigRepositoryFake()
        val sessionManager = SessionManagerFake()
        val vm = viewModel(loginRepo, configRepo, sessionManager)
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }
        vm.launchEvent(Event.OnServerUrlChange(validSelfHostedForm.serverUrl))
        vm.launchEvent(Event.OnApiKeyChange(validSelfHostedForm.apiKey))

        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()

        assertEquals(
            ServerConfig.SelfHosted(validSelfHostedForm.serverUrl, validSelfHostedForm.apiKey),
            configRepo.stored
        )
        assertEquals(1, sessionManager.openCallCount)
        assertEquals(listOf<Effect>(Effect.NavigateToStock), effects)
        collector.cancel()
    }

    @Test
    fun `a double-tap on connect fires only one validation call`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val gate = CompletableDeferred<Unit>()
        val loginRepo = LoginRepositoryFake {
            gate.await()
            ServerValidation.forVersion("4.2.0")
        }
        val configRepo = ServerConfigRepositoryFake()
        val sessionManager = SessionManagerFake()
        val vm = viewModel(loginRepo, configRepo, sessionManager)
        val effects = mutableListOf<Effect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }
        vm.launchEvent(Event.OnServerUrlChange(validSelfHostedForm.serverUrl))
        vm.launchEvent(Event.OnApiKeyChange(validSelfHostedForm.apiKey))

        // Simulate a fast double-tap: both events are queued before either completes.
        vm.launchEvent(Event.OnConnectClick)
        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()

        assertEquals(1, loginRepo.callCount)
        assertEquals(ConnectionUi.Connecting, vm.state.value.connection)

        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(1, loginRepo.callCount)
        assertEquals(1, sessionManager.openCallCount)
        assertEquals(listOf<Effect>(Effect.NavigateToStock), effects)
        collector.cancel()
    }
}
