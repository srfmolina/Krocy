package com.srfmolina.krocy.data.network

import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
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
 * Credentials are attached only to requests aimed at the configured server itself:
 * anything else (a future external image URL, a cross-host redirect target) must
 * never see the API key or the ingress cookie.
 */
private fun HttpRequestBuilder.targetsServer(config: ServerConfig): Boolean =
    url.host == Url(config.grocyBaseUrl).host

/**
 * Backstop for cross-host redirects: Ktor's HttpRedirect runs below the HttpSend
 * interceptors above and replays custom headers onto the redirect target (its
 * automatic stripping only covers Authorization). The send pipeline runs for every
 * request that actually reaches the engine - each redirect hop included - so this
 * strips our credentials from any hop that leaves the configured server's host.
 */
private fun HttpClient.stripCredentialsForForeignHosts(allowedHost: suspend () -> String?) {
    sendPipeline.intercept(HttpSendPipeline.State) {
        if (context.url.host != allowedHost()) {
            context.headers.remove("GROCY-API-KEY")
            context.headers.remove(HttpHeaders.Cookie)
        }
    }
}

/**
 * The session-scoped client shared by all generated APIs. Attaches GROCY-API-KEY
 * and, in Home Assistant mode, the ingress_session cookie; on a 401 it re-runs
 * the HA handshake once and retries. [onSessionExpired] fires only when the token
 * is genuinely rejected; transient handshake failures surface the original 401.
 */
internal fun buildSessionHttpClient(
    config: ServerConfig,
    sessionStore: HassSessionStore,
    sessionSource: HassSessionSource,
    onSessionExpired: () -> Unit,
    engine: HttpClientEngine? = null
): HttpClient = baseClient(engine).apply {
    plugin(HttpSend).intercept { request ->
        if (!request.targetsServer(config)) return@intercept execute(request)
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
            } catch (_: LoginFailure.InvalidHassToken) {
                // Only a rejected token is a real revocation. Anything else (network blip,
                // HA restarting) is transient: surface the original 401 to the caller and
                // keep the stored session so a later request retries the handshake.
                onSessionExpired()
                return@intercept call
            } catch (_: Exception) {
                return@intercept call
            }
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
        call
    }
    stripCredentialsForForeignHosts { Url(config.grocyBaseUrl).host }
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
        if (config != null && request.targetsServer(config)) {
            config.apiKey?.let { request.header("GROCY-API-KEY", it) }
            if (config is ServerConfig.HomeAssistant) {
                sessionStore.session()?.let { request.setIngressCookie(it) }
            }
        }
        execute(request)
    }
    stripCredentialsForForeignHosts { configRepository.get()?.let { Url(it.grocyBaseUrl).host } }
}
