package com.srfmolina.krocy.data.network

import com.srfmolina.krocy.domain.model.server.LoginFailure
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.withTimeout

internal interface HassSessionSource {
    /**
     * Runs the HA WebSocket handshake and returns a fresh ingress session key.
     * Throws [LoginFailure.InvalidHassToken] on auth_invalid and
     * [LoginFailure.UnreachableServer]/[LoginFailure.Unexpected] otherwise.
     */
    suspend fun acquireSession(haServerUrl: String, longLivedToken: String): String
}

private const val HANDSHAKE_TIMEOUT_MS = 15_000L

internal class KtorHassSessionSource(
    private val client: HttpClient = HttpClient { install(WebSockets) }
) : HassSessionSource {

    override suspend fun acquireSession(haServerUrl: String, longLivedToken: String): String {
        val wsUrl = haServerUrl.trimEnd('/')
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://") + "/api/websocket"
        var session: String? = null
        try {
            withTimeout(HANDSHAKE_TIMEOUT_MS) {
                client.webSocket(wsUrl) {
                    var requestId = 1
                    for (frame in incoming) {
                        val text = (frame as? Frame.Text)?.readText() ?: continue
                        when (val message = HassMessages.parse(text)) {
                            is HassMessages.Incoming.AuthRequired ->
                                send(Frame.Text(HassMessages.auth(longLivedToken)))
                            is HassMessages.Incoming.AuthOk ->
                                send(Frame.Text(HassMessages.sessionRequest(requestId++)))
                            is HassMessages.Incoming.AuthInvalid ->
                                throw LoginFailure.InvalidHassToken()
                            is HassMessages.Incoming.SessionResult -> {
                                if (message.success && message.session != null) {
                                    session = message.session
                                    close()
                                } else {
                                    throw LoginFailure.Unexpected(text)
                                }
                            }
                            is HassMessages.Incoming.Other -> Unit
                        }
                    }
                }
            }
        } catch (failure: LoginFailure) {
            throw failure
        } catch (e: Exception) {
            throw LoginFailure.UnreachableServer(e.message ?: e.toString())
        }
        return session ?: throw LoginFailure.Unexpected("WebSocket cerrado sin clave de sesión")
    }
}
