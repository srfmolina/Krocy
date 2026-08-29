package com.srfmolina.krocy.data.session

import com.srfmolina.krocy.data.di.dataModule
import com.srfmolina.krocy.domain.model.server.ServerConfig
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.openapitools.client.apis.StockApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionManagerImplTest {

    private val serverA = ServerConfig.SelfHosted(serverUrl = "https://server-a.example", apiKey = "key-a")
    private val serverB = ServerConfig.SelfHosted(serverUrl = "https://server-b.example", apiKey = "key-b")

    // SessionManagerImpl is a KoinComponent, so it resolves through the global
    // Koin context - it must be bootstrapped/torn down the same way here.
    @BeforeTest
    fun setUp() {
        startKoin { modules(dataModule) }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    private fun koin() = GlobalContext.get()

    @Test
    fun `open unloads the previous session module before loading the new one`() = runTest {
        val manager = SessionManagerImpl()

        manager.open(serverA)
        val stockApiA = koin().get<StockApi>()
        val httpClientA = koin().get<HttpClient>()
        assertTrue(httpClientA.isActive)

        manager.open(serverB)

        // The old module's definitions must be gone - a fresh HttpClient/StockApi
        // are bound for the new server, distinct from server A's instances.
        val httpClientB = koin().get<HttpClient>()
        val stockApiB = koin().get<StockApi>()
        assertNotSame(httpClientA, httpClientB)
        assertNotSame(stockApiA, stockApiB)
        assertFalse(httpClientA.isActive, "previous session's HttpClient should be closed on switch")
        assertTrue(httpClientB.isActive)
    }

    @Test
    fun `unload closes the session HttpClient`() = runTest {
        val manager = SessionManagerImpl()
        manager.open(serverA)
        val httpClient = koin().get<HttpClient>()
        assertTrue(httpClient.isActive)

        manager.close()

        assertFalse(httpClient.isActive, "HttpClient should be closed once its session is torn down")
        assertNull(koin().getOrNull<HttpClient>())
    }

    @Test
    fun `close without a prior open is a no-op`() = runTest {
        val manager = SessionManagerImpl()

        manager.close() // must not throw

        assertNull(koin().getOrNull<HttpClient>())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `concurrent open calls serialize through the mutex leaving one consistent session`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val manager = SessionManagerImpl()
        val configs = listOf(serverA, serverB, ServerConfig.Demo, serverA, serverB)

        // Fire every open() "at once" on a controlled dispatcher; the mutex must
        // serialize them so each fully unloads the previous session before the
        // next loads - no duplicate-definition errors, no dangling clients.
        val results = configs.map { config ->
            async(dispatcher) {
                manager.open(config)
                koin().get<HttpClient>()
            }
        }
        advanceUntilIdle()
        val clients = results.map { it.getCompleted() }

        // One distinct HttpClient instance per open() call: nothing was skipped
        // or double-loaded.
        assertEquals(configs.size, clients.toSet().size)
        // Every session but the last was torn down as soon as the next one loaded.
        clients.dropLast(1).forEach { assertFalse(it.isActive) }
        assertTrue(clients.last().isActive)
        assertEquals(clients.last(), koin().get<HttpClient>())
    }
}
