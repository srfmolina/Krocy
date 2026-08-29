package com.srfmolina.krocy.data.network

import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class MemorySessionStore(var value: String? = null) : HassSessionStore {
    override suspend fun session(): String? = value
    override suspend fun save(session: String) { value = session }
    override suspend fun clear() { value = null }
}

private class FakeSessionSource(
    private val next: String?, // null -> acquisition fails
    private val failure: Exception = LoginFailure.InvalidHassToken()
) : HassSessionSource {
    var acquisitions = 0
    override suspend fun acquireSession(haServerUrl: String, longLivedToken: String): String {
        acquisitions++
        return next ?: throw failure
    }
}

private class FakeConfigRepository(private val config: ServerConfig?) :
    com.srfmolina.krocy.domain.repository.ServerConfigRepository {
    override suspend fun get(): ServerConfig? = config
    override suspend fun save(config: ServerConfig) = Unit
    override suspend fun clear() = Unit
}

private val HASS_CONFIG = ServerConfig.HomeAssistant(
    haServerUrl = "http://ha.local:8123",
    ingressProxyId = "proxy",
    longLivedToken = "token",
    apiKey = "api-key"
)

class SessionHttpClientTest {

    @Test
    fun `self hosted requests carry the api key header`() = runBlocking {
        val engine = MockEngine { request ->
            assertEquals("api-key", request.headers["GROCY-API-KEY"])
            assertNull(request.headers["Cookie"])
            respond("{}", HttpStatusCode.OK)
        }
        val client = buildSessionHttpClient(
            config = ServerConfig.SelfHosted("https://grocy.casa", "api-key"),
            sessionStore = MemorySessionStore(),
            sessionSource = FakeSessionSource(null),
            onSessionExpired = { error("must not fire") },
            engine = engine
        )
        client.get("https://grocy.casa/api/system/info")
        assertEquals(1, engine.requestHistory.size)
    }

    @Test
    fun `demo requests carry no auth headers`() = runBlocking {
        val engine = MockEngine { request ->
            assertNull(request.headers["GROCY-API-KEY"])
            respond("{}", HttpStatusCode.OK)
        }
        val client = buildSessionHttpClient(
            config = ServerConfig.Demo,
            sessionStore = MemorySessionStore(),
            sessionSource = FakeSessionSource(null),
            onSessionExpired = { error("must not fire") },
            engine = engine
        )
        client.get("https://en.demo.grocy.info/api/system/info")
        assertEquals(1, engine.requestHistory.size)
    }

    @Test
    fun `hass 401 reacquires the session once and retries`() = runBlocking {
        val store = MemorySessionStore("stale")
        val source = FakeSessionSource("fresh")
        val engine = MockEngine { request ->
            if (request.headers["Cookie"] == "ingress_session=fresh") {
                respond("{}", HttpStatusCode.OK)
            } else {
                respond("unauthorized", HttpStatusCode.Unauthorized)
            }
        }
        var expired = false
        val client = buildSessionHttpClient(
            config = HASS_CONFIG,
            sessionStore = store,
            sessionSource = source,
            onSessionExpired = { expired = true },
            engine = engine
        )
        val response: HttpResponse = client.get("http://ha.local:8123/api/hassio_ingress/proxy/api/system/info")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, engine.requestHistory.size)
        assertEquals(1, source.acquisitions)
        assertEquals("fresh", store.value)
        assertFalse(expired)
    }

    @Test
    fun `hass 401 with a fresh session that also fails does not persist it`() = runBlocking {
        val store = MemorySessionStore("stale")
        val source = FakeSessionSource("fresh")
        val engine = MockEngine { respond("unauthorized", HttpStatusCode.Unauthorized) }
        var expired = false
        val client = buildSessionHttpClient(
            config = HASS_CONFIG,
            sessionStore = store,
            sessionSource = source,
            onSessionExpired = { expired = true },
            engine = engine
        )
        val response = client.get("http://ha.local:8123/api/hassio_ingress/proxy/api/system/info")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(2, engine.requestHistory.size)
        assertEquals(1, source.acquisitions)
        assertEquals("stale", store.value)
        assertTrue(expired)
    }

    @Test
    fun `hass 401 with a transient reauth failure keeps the session and does not force logout`() = runBlocking {
        val store = MemorySessionStore("stale")
        val engine = MockEngine { respond("unauthorized", HttpStatusCode.Unauthorized) }
        var expired = false
        val client = buildSessionHttpClient(
            config = HASS_CONFIG,
            sessionStore = store,
            sessionSource = FakeSessionSource(
                next = null,
                failure = LoginFailure.UnreachableServer("wifi blip")
            ),
            onSessionExpired = { expired = true },
            engine = engine
        )
        val response = client.get("http://ha.local:8123/api/hassio_ingress/proxy/api/system/info")
        // A network blip during the handshake is an ordinary request failure, not a
        // revoked token: the caller sees the original 401 and the stored session survives
        // so a later request can retry the handshake.
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertFalse(expired)
        assertEquals("stale", store.value)
    }

    @Test
    fun `self hosted 401 passes through without expiry or retry`() = runBlocking {
        val engine = MockEngine { respond("unauthorized", HttpStatusCode.Unauthorized) }
        var expired = false
        val client = buildSessionHttpClient(
            config = ServerConfig.SelfHosted("https://grocy.casa", "revoked-key"),
            sessionStore = MemorySessionStore(),
            sessionSource = FakeSessionSource(null),
            onSessionExpired = { expired = true },
            engine = engine
        )
        // Pin: in self-hosted mode there is no session to re-acquire, so a 401 (e.g. a
        // rotated API key) surfaces to the caller as-is; each screen shows its own error.
        val response = client.get("https://grocy.casa/api/system/info")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(1, engine.requestHistory.size)
        assertFalse(expired)
    }

    @Test
    fun `session client never sends credentials to a host other than the configured server`() = runBlocking {
        val engine = MockEngine { request ->
            assertNull(request.headers["GROCY-API-KEY"])
            assertNull(request.headers["Cookie"])
            respond("{}", HttpStatusCode.OK)
        }
        val client = buildSessionHttpClient(
            config = HASS_CONFIG,
            sessionStore = MemorySessionStore("session"),
            sessionSource = FakeSessionSource(null),
            onSessionExpired = { error("must not fire") },
            engine = engine
        )
        client.get("https://evil.example.com/steal")
        assertEquals(1, engine.requestHistory.size)
    }

    @Test
    fun `session client does not replay credentials on a cross-host redirect`() = runBlocking {
        val engine = MockEngine { request ->
            when (request.url.host) {
                "grocy.casa" -> respond(
                    "",
                    HttpStatusCode.Found,
                    headersOf(HttpHeaders.Location, "https://evil.example.com/steal")
                )
                else -> {
                    assertNull(request.headers["GROCY-API-KEY"])
                    respond("{}", HttpStatusCode.OK)
                }
            }
        }
        val client = buildSessionHttpClient(
            config = ServerConfig.SelfHosted("https://grocy.casa", "api-key"),
            sessionStore = MemorySessionStore(),
            sessionSource = FakeSessionSource(null),
            onSessionExpired = { error("must not fire") },
            engine = engine
        )
        client.get("https://grocy.casa/api/system/info")
        assertEquals(2, engine.requestHistory.size)
    }

    @Test
    fun `image client never sends credentials to a host other than the configured server`() = runBlocking {
        val engine = MockEngine { request ->
            assertNull(request.headers["GROCY-API-KEY"])
            assertNull(request.headers["Cookie"])
            respond("{}", HttpStatusCode.OK)
        }
        val client = buildImageHttpClient(
            configRepository = FakeConfigRepository(HASS_CONFIG),
            sessionStore = MemorySessionStore("session"),
            engine = engine
        )
        client.get("https://evil.example.com/image.png")
        assertEquals(1, engine.requestHistory.size)
    }

    @Test
    fun `image client sends credentials to the configured server`() = runBlocking {
        val engine = MockEngine { request ->
            assertEquals("api-key", request.headers["GROCY-API-KEY"])
            assertEquals("ingress_session=session", request.headers["Cookie"])
            respond("{}", HttpStatusCode.OK)
        }
        val client = buildImageHttpClient(
            configRepository = FakeConfigRepository(HASS_CONFIG),
            sessionStore = MemorySessionStore("session"),
            engine = engine
        )
        client.get("http://ha.local:8123/api/hassio_ingress/proxy/api/files/productpictures/x")
        assertEquals(1, engine.requestHistory.size)
    }

    @Test
    fun `hass 401 with failing reauth signals session expiry`() = runBlocking {
        val engine = MockEngine { respond("unauthorized", HttpStatusCode.Unauthorized) }
        var expired = false
        val client = buildSessionHttpClient(
            config = HASS_CONFIG,
            sessionStore = MemorySessionStore("stale"),
            sessionSource = FakeSessionSource(null),
            onSessionExpired = { expired = true },
            engine = engine
        )
        val response = client.get("http://ha.local:8123/api/hassio_ingress/proxy/api/system/info")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(1, engine.requestHistory.size)
        assertTrue(expired)
    }
}
