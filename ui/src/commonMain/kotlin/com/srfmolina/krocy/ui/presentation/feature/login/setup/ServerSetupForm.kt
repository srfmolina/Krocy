package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.GrocyQrCredentials
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.model.server.hasHttpScheme
import com.srfmolina.krocy.domain.model.server.normalizeUrlScheme

internal sealed interface ValidationResult {
    data class Valid(val config: ServerConfig) : ValidationResult
    data class Invalid(
        val serverUrlError: String? = null,
        val apiKeyError: String? = null,
        val haTokenError: String? = null,
        val proxyIdError: String? = null
    ) : ValidationResult
}

private const val ERROR_REQUIRED = "Campo obligatorio"
private const val ERROR_URL = "URL no válida: debe empezar por http:// o https://"

internal data class ServerSetupForm(
    val usingHass: Boolean = false,
    val serverUrl: String = "",
    val apiKey: String = "",
    val haToken: String = "",
    val ingressProxyId: String = ""
) {
    fun validate(): ValidationResult {
        val url = serverUrl.trim().trimEnd('/')
        val key = apiKey.trim()
        val token = haToken.trim()
        val proxyId = ingressProxyId.trim()

        val serverUrlError = when {
            url.isEmpty() -> ERROR_REQUIRED
            !hasHttpScheme(url) -> ERROR_URL
            else -> null
        }
        val apiKeyError = if (key.isEmpty()) ERROR_REQUIRED else null
        val haTokenError = if (usingHass && token.isEmpty()) ERROR_REQUIRED else null
        val proxyIdError = if (usingHass && proxyId.isEmpty()) ERROR_REQUIRED else null

        if (serverUrlError != null || apiKeyError != null ||
            haTokenError != null || proxyIdError != null
        ) {
            return ValidationResult.Invalid(serverUrlError, apiKeyError, haTokenError, proxyIdError)
        }
        val normalizedUrl = normalizeUrlScheme(url)
        return ValidationResult.Valid(
            if (usingHass) {
                ServerConfig.HomeAssistant(
                    haServerUrl = normalizedUrl,
                    ingressProxyId = proxyId,
                    longLivedToken = token,
                    apiKey = key
                )
            } else {
                ServerConfig.SelfHosted(serverUrl = normalizedUrl, apiKey = key)
            }
        )
    }

    /**
     * Fills the form from a scanned QR. `haToken` is never touched: a Grocy QR cannot carry the
     * Home Assistant long-lived token, so whatever the user typed must survive the scan.
     */
    fun applyQr(credentials: GrocyQrCredentials): ServerSetupForm = when (credentials) {
        is GrocyQrCredentials.SelfHosted -> copy(
            usingHass = false,
            serverUrl = credentials.serverUrl,
            apiKey = credentials.apiKey,
            ingressProxyId = ""
        )
        is GrocyQrCredentials.HomeAssistant -> copy(
            usingHass = true,
            serverUrl = credentials.haServerUrl,
            ingressProxyId = credentials.ingressProxyId,
            apiKey = credentials.apiKey
        )
    }
}
