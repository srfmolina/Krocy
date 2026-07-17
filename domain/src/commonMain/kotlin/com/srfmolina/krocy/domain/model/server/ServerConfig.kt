package com.srfmolina.krocy.domain.model.server

/**
 * The single configured Grocy server. Persisted between launches; absence means
 * the user has not completed login yet.
 */
sealed interface ServerConfig {

    /** Root URL of the Grocy instance (no trailing slash, without `/api`). */
    val grocyBaseUrl: String

    /** REST API root used by the generated clients. */
    val apiBaseUrl: String get() = "$grocyBaseUrl/api"

    /** Grocy API key; null only for the demo server, which accepts anonymous access. */
    val apiKey: String?

    data object Demo : ServerConfig {
        override val grocyBaseUrl: String = "https://en.demo.grocy.info"
        override val apiKey: String? = null
    }

    data class SelfHosted(
        val serverUrl: String,
        override val apiKey: String
    ) : ServerConfig {
        override val grocyBaseUrl: String get() = serverUrl.trimEnd('/')
    }

    data class HomeAssistant(
        val haServerUrl: String,
        val ingressProxyId: String,
        val longLivedToken: String,
        override val apiKey: String
    ) : ServerConfig {
        override val grocyBaseUrl: String
            get() = "${haServerUrl.trimEnd('/')}/api/hassio_ingress/$ingressProxyId"
    }
}
