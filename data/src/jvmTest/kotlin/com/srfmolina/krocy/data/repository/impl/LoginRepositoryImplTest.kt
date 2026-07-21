package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.data.network.HassSessionSource
import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val SYSTEM_INFO_OK =
    """{"grocy_version":{"Version":"4.5.0","ReleaseDate":"2025-06-01"},"php_version":"8.3"}"""

private class MemorySessionStore(var value: String? = null) : HassSessionStore {
    override suspend fun session(): String? = value
    override suspend fun save(session: String) { value = session }
    override suspend fun clear() { value = null }
}

private class FakeSessionSource(private val next: String?) : HassSessionSource {
    override suspend fun acquireSession(haServerUrl: String, longLivedToken: String): String =
        next ?: throw LoginFailure.InvalidHassToken()
}

private val SELF = ServerConfig.SelfHosted("https://grocy.casa", "key")
private val HASS = ServerConfig.HomeAssistant("http://ha.local:8123", "proxy", "token", "key")

class LoginRepositoryImplTest {

    private fun repository(
        engine: MockEngine,
        sessionSource: HassSessionSource = FakeSessionSource("session-1"),
        sessionStore: HassSessionStore = MemorySessionStore()
    ) = LoginRepositoryImpl(sessionSource, sessionStore, engine)

    @Test
    fun `valid grocy 4 server is supported`() = runBlocking {
        val engine = MockEngine { request ->
            assertEquals("https://grocy.casa/api/system/info", request.url.toString())
            assertEquals("key", request.headers["GROCY-API-KEY"])
            respond(SYSTEM_INFO_OK, HttpStatusCode.OK,
                headersOf("Content-Type", "application/json"))
        }
        val validation = repository(engine).validate(SELF)
        assertEquals("4.5.0", validation.grocyVersion)
        assertTrue(validation.isSupported)
    }

    @Test
    fun `grocy 3 server is valid but unsupported`() = runBlocking {
        val body = SYSTEM_INFO_OK.replace("4.5.0", "3.3.2")
        val engine = MockEngine {
            respond(body, HttpStatusCode.OK, headersOf("Content-Type", "application/json"))
        }
        assertFalse(repository(engine).validate(SELF).isSupported)
    }

    @Test
    fun `401 on self hosted means bad api key`(): Unit = runBlocking {
        val engine = MockEngine { respond("", HttpStatusCode.Unauthorized) }
        assertFailsWith<LoginFailure.InvalidApiKey> { repository(engine).validate(SELF) }
    }

    @Test
    fun `404 means not a grocy instance`(): Unit = runBlocking {
        val engine = MockEngine { respond("<html>not found</html>", HttpStatusCode.NotFound) }
        assertFailsWith<LoginFailure.NotGrocyInstance> { repository(engine).validate(SELF) }
    }

    @Test
    fun `response without grocy_version means not a grocy instance`(): Unit = runBlocking {
        val engine = MockEngine {
            respond("""{"hello":"world"}""", HttpStatusCode.OK,
                headersOf("Content-Type", "application/json"))
        }
        assertFailsWith<LoginFailure.NotGrocyInstance> { repository(engine).validate(SELF) }
    }

    @Test
    fun `hass 503 means wrong ingress proxy id`(): Unit = runBlocking {
        val engine = MockEngine { respond("", HttpStatusCode.ServiceUnavailable) }
        assertFailsWith<LoginFailure.WrongIngressProxy> { repository(engine).validate(HASS) }
    }

    @Test
    fun `hass 401 means bad long lived token`(): Unit = runBlocking {
        val engine = MockEngine { respond("", HttpStatusCode.Unauthorized) }
        assertFailsWith<LoginFailure.InvalidHassToken> { repository(engine).validate(HASS) }
    }

    @Test
    fun `hass validation acquires a session and stores it once validation succeeds`() = runBlocking {
        val store = MemorySessionStore()
        val engine = MockEngine { request ->
            assertEquals("ingress_session=session-1", request.headers["Cookie"])
            respond(SYSTEM_INFO_OK, HttpStatusCode.OK,
                headersOf("Content-Type", "application/json"))
        }
        repository(engine, sessionStore = store).validate(HASS)
        assertEquals("session-1", store.value)
    }

    @Test
    fun `hass validation failure does not persist the acquired ingress session`(): Unit = runBlocking {
        val store = MemorySessionStore()
        val engine = MockEngine { respond("", HttpStatusCode.Unauthorized) }
        assertFailsWith<LoginFailure.InvalidHassToken> {
            repository(engine, sessionStore = store).validate(HASS)
        }
        assertEquals(null, store.value)
    }

    @Test
    fun `connection errors map to unreachable`(): Unit = runBlocking {
        val engine = MockEngine { throw java.net.ConnectException("Connection refused") }
        assertFailsWith<LoginFailure.UnreachableServer> { repository(engine).validate(SELF) }
    }
}
