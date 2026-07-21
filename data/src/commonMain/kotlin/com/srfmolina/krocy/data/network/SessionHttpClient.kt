package com.srfmolina.krocy.data.network

import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

/** Same relaxed JSON settings as the generated ApiClient.JSON_DEFAULT. */
internal val grocyJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private fun baseClient(engine: HttpClientEngine?): HttpClient {
    val configure: HttpClientConfig<*>.() -> Unit = {
        install(ContentNegotiation) { json(grocyJson) }
    }
    return engine?.let { HttpClient(it, configure) } ?: HttpClient(configure)
}

private fun HttpRequestBuilder.setIngressCookie(session: String) {
    headers.remove(HttpHeaders.Cookie)
    header(HttpHeaders.Cookie, "ingress_session=$session")
}

/**
 * The session-scoped client shared by all generated APIs. Attaches GROCY-API-KEY
 * and, in Home Assistant mode, the ingress_session cookie; on a 401 it re-runs
 * the HA handshake once and retries. If re-auth fails, [onSessionExpired] fires.
 */
internal fun buildSessionHttpClient(
    config: ServerConfig,
    sessionStore: HassSessionStore,
    sessionSource: HassSessionSource,
    onSessionExpired: () -> Unit,
    engine: HttpClientEngine? = null
): HttpClient = baseClient(engine).apply {
    plugin(HttpSend).intercept { request ->
        config.apiKey?.let { request.header("GROCY-API-KEY", it) }
        if (config is ServerConfig.HomeAssistant) {
            sessionStore.session()?.let { request.setIngressCookie(it) }
        }
        var call = execute(request)
        if (call.response.status == HttpStatusCode.Unauthorized && config is ServerConfig.HomeAssistant) {
            val fresh = try {
                sessionSource.acquireSession(config.haServerUrl, config.longLivedToken)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
            if (fresh == null) {
                onSessionExpired()
            } else {
                request.setIngressCookie(fresh)
                call = execute(request)
                if (call.response.status == HttpStatusCode.Unauthorized) {
                    // The freshly-acquired session was evidently invalid too - don't persist
                    // it, or every later request pays a doomed re-auth handshake against it.
                    onSessionExpired()
                } else {
                    sessionStore.save(fresh)
                }
            }
        }
        call
    }
}

/**
 * Long-lived client for image loading (Coil). Survives login/logout by reading
 * the current config on every request. No 401 retry: a stale image just fails.
 */
internal fun buildImageHttpClient(
    configRepository: ServerConfigRepository,
    sessionStore: HassSessionStore,
    engine: HttpClientEngine? = null
): HttpClient = baseClient(engine).apply {
    plugin(HttpSend).intercept { request ->
        val config = configRepository.get()
        config?.apiKey?.let { request.header("GROCY-API-KEY", it) }
        if (config is ServerConfig.HomeAssistant) {
            sessionStore.session()?.let { request.setIngressCookie(it) }
        }
        execute(request)
    }
}
