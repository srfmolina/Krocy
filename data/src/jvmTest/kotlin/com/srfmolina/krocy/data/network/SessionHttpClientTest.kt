package com.srfmolina.krocy.data.network

import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
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
    private val next: String? // null -> acquisition fails
) : HassSessionSource {
    var acquisitions = 0
    override suspend fun acquireSession(haServerUrl: String, longLivedToken: String): String {
        acquisitions++
        return next ?: throw LoginFailure.InvalidHassToken()
    }
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
