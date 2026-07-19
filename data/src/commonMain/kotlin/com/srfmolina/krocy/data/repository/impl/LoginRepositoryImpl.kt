package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.data.network.HassSessionSource
import com.srfmolina.krocy.data.network.grocyJson
import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.model.server.ServerValidation
import com.srfmolina.krocy.domain.repository.LoginRepository
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val VALIDATION_TIMEOUT_MS = 15_000L

/**
 * Tests a candidate config with a one-off client (the session graph opens only
 * after validation succeeds). Mirrors grocy-android's login test:
 * GET /system/info must answer with a grocy_version.
 */
internal class LoginRepositoryImpl(
    private val sessionSource: HassSessionSource,
    private val sessionStore: HassSessionStore,
    private val engine: HttpClientEngine? = null
) : LoginRepository {

    override suspend fun validate(config: ServerConfig): ServerValidation {
        val ingressSession = if (config is ServerConfig.HomeAssistant) {
            sessionSource.acquireSession(config.haServerUrl, config.longLivedToken)
                .also { sessionStore.save(it) }
        } else null

        val client = buildClient()
        try {
            val response = try {
                client.get("${config.apiBaseUrl}/system/info") {
                    config.apiKey?.let { header("GROCY-API-KEY", it) }
                    ingressSession?.let { header(HttpHeaders.Cookie, "ingress_session=$it") }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpRequestTimeoutException) {
                throw LoginFailure.Timeout(e.message ?: "timeout")
            } catch (e: Exception) {
                throw mapConnectionError(e)
            }

            val isHass = config is ServerConfig.HomeAssistant
            when {
                response.status == HttpStatusCode.Unauthorized && isHass ->
                    throw LoginFailure.InvalidHassToken()
                response.status == HttpStatusCode.Unauthorized ->
                    throw LoginFailure.InvalidApiKey()
                response.status == HttpStatusCode.NotFound ->
                    throw LoginFailure.NotGrocyInstance("HTTP 404")
                response.status == HttpStatusCode.ServiceUnavailable && isHass ->
                    throw LoginFailure.WrongIngressProxy("HTTP 503")
                !response.status.isSuccessStatusCode() ->
                    throw LoginFailure.Unexpected("HTTP ${response.status.value}")
            }

            val version = parseGrocyVersion(response.bodyAsText())
                ?: throw LoginFailure.NotGrocyInstance("La respuesta no contiene grocy_version")
            return ServerValidation.forVersion(version)
        } finally {
            client.close()
        }
    }

    private fun buildClient(): HttpClient {
        val configure: HttpClientConfig<*>.() -> Unit = {
            install(HttpTimeout) {
                requestTimeoutMillis = VALIDATION_TIMEOUT_MS
                connectTimeoutMillis = VALIDATION_TIMEOUT_MS
            }
            expectSuccess = false
        }
        return engine?.let { HttpClient(it, configure) } ?: HttpClient(configure)
    }

    private fun parseGrocyVersion(body: String): String? = runCatching {
        grocyJson.parseToJsonElement(body)
            .jsonObject["grocy_version"]!!
            .jsonObject["Version"]!!
            .jsonPrimitive.content
    }.getOrNull()

    private fun mapConnectionError(e: Exception): LoginFailure {
        val text = buildString {
            var cause: Throwable? = e
            while (cause != null) {
                append(cause::class.simpleName).append(": ").append(cause.message).append('\n')
                cause = cause.cause?.takeIf { it !== cause }
            }
        }.trim()
        return when {
            text.contains("SSL", ignoreCase = true) ||
                text.contains("certificate", ignoreCase = true) ||
                text.contains("handshake", ignoreCase = true) -> LoginFailure.SslHandshake(text)
            text.contains("timeout", ignoreCase = true) -> LoginFailure.Timeout(text)
            else -> LoginFailure.UnreachableServer(text)
        }
    }
}

private fun HttpStatusCode.isSuccessStatusCode() = value in 200..299
