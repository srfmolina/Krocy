package com.srfmolina.krocy.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.srfmolina.krocy.data.config.CredentialCipher
import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val KEY_MODE = stringPreferencesKey("mode") // "demo" | "self" | "hass"
private val KEY_SERVER_URL = stringPreferencesKey("server_url")
private val KEY_API_KEY = stringPreferencesKey("api_key_enc")
private val KEY_HA_URL = stringPreferencesKey("ha_url")
private val KEY_HA_PROXY_ID = stringPreferencesKey("ha_proxy_id")
private val KEY_HA_TOKEN = stringPreferencesKey("ha_token_enc")
private val KEY_INGRESS_SESSION = stringPreferencesKey("ingress_session_enc")

internal class ServerConfigRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val cipher: CredentialCipher
) : ServerConfigRepository, HassSessionStore {

    override val config: Flow<ServerConfig?> = dataStore.data.map { it.toConfig() }

    override suspend fun get(): ServerConfig? = config.first()

    override suspend fun save(config: ServerConfig) {
        dataStore.edit { prefs ->
            when (config) {
                is ServerConfig.Demo -> {
                    prefs[KEY_MODE] = "demo"
                    prefs.remove(KEY_SERVER_URL); prefs.remove(KEY_API_KEY)
                    prefs.remove(KEY_HA_URL); prefs.remove(KEY_HA_PROXY_ID); prefs.remove(KEY_HA_TOKEN)
                }
                is ServerConfig.SelfHosted -> {
                    prefs[KEY_MODE] = "self"
                    prefs[KEY_SERVER_URL] = config.serverUrl
                    prefs[KEY_API_KEY] = cipher.encrypt(config.apiKey)
                    prefs.remove(KEY_HA_URL); prefs.remove(KEY_HA_PROXY_ID); prefs.remove(KEY_HA_TOKEN)
                }
                is ServerConfig.HomeAssistant -> {
                    prefs[KEY_MODE] = "hass"
                    prefs[KEY_HA_URL] = config.haServerUrl
                    prefs[KEY_HA_PROXY_ID] = config.ingressProxyId
                    prefs[KEY_HA_TOKEN] = cipher.encrypt(config.longLivedToken)
                    prefs[KEY_API_KEY] = cipher.encrypt(config.apiKey)
                    prefs.remove(KEY_SERVER_URL)
                }
            }
        }
    }

    // Satisfies both ServerConfigRepository.clear() and HassSessionStore.clear() (same
    // signature, merged into one override): wipes the whole store, config and cached
    // ingress session alike. Only called from logout paths, where that's the right result.
    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    // --- HassSessionStore ---

    override suspend fun session(): String? =
        dataStore.data.first()[KEY_INGRESS_SESSION]?.let(cipher::decrypt)

    override suspend fun save(session: String) {
        dataStore.edit { it[KEY_INGRESS_SESSION] = cipher.encrypt(session) }
    }

    private fun Preferences.toConfig(): ServerConfig? = when (this[KEY_MODE]) {
        "demo" -> ServerConfig.Demo
        "self" -> {
            val url = this[KEY_SERVER_URL]
            val key = this[KEY_API_KEY]?.let(cipher::decrypt)
            if (url != null && key != null) ServerConfig.SelfHosted(url, key) else null
        }
        "hass" -> {
            val url = this[KEY_HA_URL]
            val proxyId = this[KEY_HA_PROXY_ID]
            val token = this[KEY_HA_TOKEN]?.let(cipher::decrypt)
            val key = this[KEY_API_KEY]?.let(cipher::decrypt)
            if (url != null && proxyId != null && token != null && key != null) {
                ServerConfig.HomeAssistant(url, proxyId, token, key)
            } else null
        }
        else -> null
    }
}
