package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.ServerConfig

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

        // URI schemes are case-insensitive (RFC 3986), so accept e.g. "HTTPS://" as well -
        // pasted URLs and mobile autocapitalization both produce mixed-case schemes.
        val serverUrlError = when {
            url.isEmpty() -> ERROR_REQUIRED
            !url.startsWith("http://", ignoreCase = true) &&
                !url.startsWith("https://", ignoreCase = true) -> ERROR_URL
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
        // Normalize only the scheme to lowercase for consistent storage/equality; the host and
        // path are left untouched since they may be legitimately case-sensitive.
        val normalizedUrl = when {
            url.startsWith("https://", ignoreCase = true) -> "https://" + url.substring("https://".length)
            url.startsWith("http://", ignoreCase = true) -> "http://" + url.substring("http://".length)
            else -> url
        }
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
}
