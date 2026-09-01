package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.GrocyQrCredentials
import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.QrFrame
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.model.server.ServerValidation
import com.srfmolina.krocy.domain.repository.LoginRepository
import com.srfmolina.krocy.domain.repository.QrScannerRepository
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.login.CompleteLoginUseCase
import com.srfmolina.krocy.domain.usecase.login.ScanGrocyQrUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    private class QrScannerRepositoryFake(
        var onDecode: suspend (QrFrame) -> String? = { null }
    ) : QrScannerRepository {
        var callCount = 0
        override suspend fun decode(frame: QrFrame): String? {
            callCount++
            return onDecode(frame)
        }
    }

    private fun viewModel(
        loginRepo: LoginRepository,
        configRepo: ServerConfigRepository = ServerConfigRepositoryFake(),
        sessionManager: SessionManager = SessionManagerFake(),
        qrRepo: QrScannerRepository = QrScannerRepositoryFake()
    ) = ServerSetupViewModel(
        validateServer = ValidateServerUseCase(loginRepo),
        completeLogin = CompleteLoginUseCase(configRepo, sessionManager),
        scanGrocyQr = ScanGrocyQrUseCase(qrRepo)
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
    fun `a field edit after a failed validation clears fieldErrors`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginRepo = LoginRepositoryFake { error("must not be called") }
        val vm = viewModel(loginRepo)
        vm.launchEvent(Event.OnConnectClick)
        advanceUntilIdle()
        assertEquals(
            ValidationResult.Invalid(serverUrlError = "Campo obligatorio", apiKeyError = "Campo obligatorio"),
            vm.state.value.fieldErrors
        )

        vm.launchEvent(Event.OnServerUrlChange(validSelfHostedForm.serverUrl))
        advanceUntilIdle()

        assertNull(vm.state.value.fieldErrors)
    }

    @Test
    fun `a field edit after a connection Error resets connection to Idle`() = runTest {
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

        vm.launchEvent(Event.OnServerUrlChange("https://other.example.com"))
        advanceUntilIdle()

        assertEquals(ConnectionUi.Idle, vm.state.value.connection)
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

    private val anyFrame = QrFrame(luminance = ByteArray(4), width = 2, height = 2)

    @Test
    fun `scan click opens the scanner`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) })

        vm.launchEvent(Event.OnScanQrClick)
        advanceUntilIdle()

        assertTrue(vm.currentState.isScanning)
    }

    @Test
    fun `dismissing closes the scanner`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) })

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrScannerDismiss)
        advanceUntilIdle()

        assertFalse(vm.currentState.isScanning)
    }

    @Test
    fun `a grocy qr closes the scanner and asks the user to confirm the host`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { "http://ha.local:8123/api/hassio_ingress/px/api|k" }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrFrame(anyFrame))
        advanceUntilIdle()

        assertFalse(vm.currentState.isScanning)
        // Nothing is applied until the user vouches for the host.
        assertEquals(ServerSetupForm(), vm.currentState.form)
        val connection = vm.currentState.connection
        assertIs<ConnectionUi.QrConfirmation>(connection)
        assertEquals(
            GrocyQrCredentials.HomeAssistant("http://ha.local:8123", "px", "k"),
            connection.credentials
        )
        assertFalse(connection.isCleartext) // .local is the user's own network
    }

    @Test
    fun `confirming applies the scanned credentials to the form`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { "http://ha.local:8123/api/hassio_ingress/px/api|k" }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrFrame(anyFrame))
        advanceUntilIdle()
        vm.launchEvent(Event.OnQrConfirm)
        advanceUntilIdle()

        assertTrue(vm.currentState.form.usingHass)
        assertEquals("http://ha.local:8123", vm.currentState.form.serverUrl)
        assertEquals("px", vm.currentState.form.ingressProxyId)
        assertEquals("k", vm.currentState.form.apiKey)
        assertEquals(ConnectionUi.Idle, vm.currentState.connection)
    }

    @Test
    fun `rejecting the confirmation leaves the form untouched`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { "https://evil.host/api|stolen" }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrFrame(anyFrame))
        advanceUntilIdle()
        vm.launchEvent(Event.OnQrReject)
        advanceUntilIdle()

        assertEquals(ServerSetupForm(), vm.currentState.form)
        assertEquals(ConnectionUi.Idle, vm.currentState.connection)
    }

    @Test
    fun `a cleartext public host is flagged in the confirmation`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { "http://grocy.midominio.com/api|k" }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrFrame(anyFrame))
        advanceUntilIdle()

        val connection = vm.currentState.connection
        assertIs<ConnectionUi.QrConfirmation>(connection)
        assertTrue(connection.isCleartext)
    }

    @Test
    fun `a non grocy qr closes the scanner and shows an error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { "WIFI:S:MiRed;;" }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrFrame(anyFrame))
        advanceUntilIdle()

        assertFalse(vm.currentState.isScanning)
        val connection = vm.currentState.connection
        assertIs<ConnectionUi.Error>(connection)
        assertEquals("El código QR no pertenece a un servidor Grocy", connection.message)
    }

    @Test
    fun `a frame without a qr changes nothing and keeps scanning`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { null }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrFrame(anyFrame))
        advanceUntilIdle()

        assertTrue(vm.currentState.isScanning)
        assertEquals(ServerSetupForm(), vm.currentState.form)
    }

    @Test
    fun `frames arriving while a decode is in flight are dropped`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val gate = CompletableDeferred<Unit>()
        val qrRepo = QrScannerRepositoryFake {
            gate.await()
            "https://grocy.casa/api|key"
        }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        advanceUntilIdle()
        repeat(5) { vm.launchEvent(Event.OnQrFrame(anyFrame)) }
        advanceUntilIdle()

        assertEquals(1, qrRepo.callCount) // the camera outruns the decoder; only one gets through
        gate.complete(Unit)
        advanceUntilIdle()
        assertIs<ConnectionUi.QrConfirmation>(vm.currentState.connection)
    }

    @Test
    fun `frames arriving after the scanner closed are ignored`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { "https://grocy.casa/api|key" }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnQrFrame(anyFrame)) // scanner never opened
        advanceUntilIdle()

        assertEquals(0, qrRepo.callCount)
        assertEquals(ServerSetupForm(), vm.currentState.form)
    }

    @Test
    fun `editing a field keeps a pending qr confirmation`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val qrRepo = QrScannerRepositoryFake { "https://grocy.casa/api|key" }
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) }, qrRepo = qrRepo)

        vm.launchEvent(Event.OnScanQrClick)
        vm.launchEvent(Event.OnQrFrame(anyFrame))
        advanceUntilIdle()
        vm.launchEvent(Event.OnApiKeyChange("typed"))
        advanceUntilIdle()

        assertIs<ConnectionUi.QrConfirmation>(vm.currentState.connection)
    }

    @Test
    fun `OnQrConfirm is a no-op when there is no pending confirmation`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = viewModel(LoginRepositoryFake { ServerValidation("4.0.0", true) })

        vm.launchEvent(Event.OnQrConfirm)
        advanceUntilIdle()

        assertEquals(ServerSetupForm(), vm.currentState.form)
    }
}
