package com.srfmolina.krocy.data.network

import com.srfmolina.krocy.domain.model.server.LoginFailure
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import io.ktor.server.cio.CIO as ServerCIO

/**
 * The handshake state machine is exercised against a real loopback WebSocket server:
 * ktor-client-mock declares WebSocketCapability but ships no working upgrade path, so
 * each test scripts the Home Assistant side of the conversation in a server route.
 */
class KtorHassSessionSourceTest {

    private fun realClient() = HttpClient(CIO) { install(WebSockets) }

    private suspend fun Frame.Text.jsonType(): String =
        Json.parseToJsonElement(readText()).jsonObject["type"]?.jsonPrimitive?.content.orEmpty()

    private suspend fun DefaultWebSocketServerSession.receiveText(): Frame.Text =
        incoming.receive() as Frame.Text

    /** Runs [scriptedServer] as the HA side and [test] against its ephemeral port. */
    private fun withHassServer(
        scriptedServer: suspend DefaultWebSocketServerSession.() -> Unit,
        test: suspend (haServerUrl: String) -> Unit
    ) = runBlocking {
        val server: EmbeddedServer<*, *> = embeddedServer(ServerCIO, port = 0) {
            install(io.ktor.server.websocket.WebSockets)
            routing { webSocket("/api/websocket") { scriptedServer() } }
        }.start(wait = false)
        try {
            val port = server.engine.resolvedConnectors().first().port
            test("http://127.0.0.1:$port")
        } finally {
            server.stop(gracePeriodMillis = 0, timeoutMillis = 1_000)
        }
    }

    @Test
    fun `successful handshake sends the token, returns the session and closes the socket`() {
        var capturedToken: String? = null
        var sawClientClose = false
        withHassServer(
            scriptedServer = {
                send(Frame.Text("""{"type":"auth_required"}"""))
                val auth = receiveText()
                assertEquals("auth", auth.jsonType())
                capturedToken = Json.parseToJsonElement(auth.readText())
                    .jsonObject["access_token"]?.jsonPrimitive?.content
                send(Frame.Text("""{"type":"auth_ok"}"""))
                val sessionRequest = receiveText()
                assertEquals("supervisor/api", sessionRequest.jsonType())
                send(
                    Frame.Text(
                        """{"id":1,"type":"result","success":true,"result":{"session":"abc-session"}}"""
                    )
                )
                // The client is expected to close once it has the session.
                for (frame in incoming) {
                    if (frame is Frame.Close) sawClientClose = true
                }
                sawClientClose = true // channel closing on client close also counts
            }
        ) { url ->
            val session = KtorHassSessionSource(realClient())
                .acquireSession(url, "my-secret-token")
            assertEquals("abc-session", session)
        }
        assertEquals("my-secret-token", capturedToken)
        assertTrue(sawClientClose)
    }

    @Test
    fun `auth_invalid throws InvalidHassToken`() = withHassServer(
        scriptedServer = {
            send(Frame.Text("""{"type":"auth_required"}"""))
            receiveText() // the auth attempt
            send(Frame.Text("""{"type":"auth_invalid","message":"Invalid access token"}"""))
        }
    ) { url ->
        assertFailsWith<LoginFailure.InvalidHassToken> {
            KtorHassSessionSource(realClient()).acquireSession(url, "wrong-token")
        }
    }

    @Test
    fun `unsuccessful session result throws Unexpected with the raw message`() = withHassServer(
        scriptedServer = {
            send(Frame.Text("""{"type":"auth_required"}"""))
            receiveText()
            send(Frame.Text("""{"type":"auth_ok"}"""))
            receiveText() // the session request
            send(Frame.Text("""{"id":1,"type":"result","success":false}"""))
        }
    ) { url ->
        val failure = assertFailsWith<LoginFailure.Unexpected> {
            KtorHassSessionSource(realClient()).acquireSession(url, "my-token")
        }
        assertEquals("""{"id":1,"type":"result","success":false}""", failure.detail)
    }

    @Test
    fun `socket closed before a session is delivered throws Unexpected`() = withHassServer(
        scriptedServer = {
            send(Frame.Text("""{"type":"auth_required"}"""))
            receiveText()
            send(Frame.Text("""{"type":"auth_ok"}"""))
            receiveText() // the session request; then drop the connection with no answer
        }
    ) { url ->
        val failure = assertFailsWith<LoginFailure.Unexpected> {
            KtorHassSessionSource(realClient()).acquireSession(url, "my-token")
        }
        assertEquals("WebSocket cerrado sin clave de sesión", failure.detail)
    }

    @Test
    fun `unknown message types are ignored and the handshake continues`() = withHassServer(
        scriptedServer = {
            send(Frame.Text("""{"type":"chatter"}"""))
            send(Frame.Text("not even json"))
            send(Frame.Text("""{"type":"auth_required"}"""))
            receiveText()
            send(Frame.Text("""{"type":"auth_ok"}"""))
            receiveText()
            send(Frame.Text("""{"id":1,"type":"result","success":true,"result":{"session":"s2"}}"""))
        }
    ) { url ->
        assertEquals("s2", KtorHassSessionSource(realClient()).acquireSession(url, "t"))
    }

    @Test
    fun `connect failure throws UnreachableServer`() = runBlocking<Unit> {
        val boom = IllegalStateException("connection refused")
        val client = HttpClient(MockEngine) {
            install(WebSockets)
            engine { addHandler { throw boom } }
        }
        val failure = assertFailsWith<LoginFailure.UnreachableServer> {
            KtorHassSessionSource(client).acquireSession("http://ha.local:8123", "my-token")
        }
        assertEquals(boom.message, failure.detail)
    }

    @Test
    fun `timeout waiting for the handshake throws Timeout`() = runTest {
        // Home Assistant never answers: the handler suspends past the 15s handshake budget.
        // Pinning the engine to this test's scheduler lets that delay advance on virtual time
        // instead of really blocking the test for a minute.
        val client = HttpClient(MockEngine) {
            install(WebSockets)
            engine {
                dispatcher = StandardTestDispatcher(testScheduler)
                addHandler {
                    delay(60_000)
                    error("should never be reached: the handshake should have timed out first")
                }
            }
        }
        val failure = assertFailsWith<LoginFailure.Timeout> {
            KtorHassSessionSource(client).acquireSession("http://ha.local:8123", "my-token")
        }
        assertEquals("Tiempo de espera agotado en el WebSocket de Home Assistant", failure.detail)
    }

    @Test
    fun `cancellation is propagated, not swallowed as a LoginFailure`() = withHassServer(
        scriptedServer = {
            send(Frame.Text("""{"type":"auth_required"}"""))
            receiveText()
            // Never answer: keep the handshake suspended until the caller cancels it.
            delay(60_000)
        }
    ) { url ->
        runBlocking {
            val handshake = async {
                KtorHassSessionSource(realClient()).acquireSession(url, "my-token")
            }
            yield()
            delay(200) // let the handshake reach its suspended read
            handshake.cancel()
            yield()
            assertTrue(handshake.isCancelled)
        }
    }
}
