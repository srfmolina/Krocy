package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ServerSetupFormTest {

    @Test
    fun `valid self hosted form builds a trimmed config`() {
        val result = ServerSetupForm(
            serverUrl = "  https://grocy.casa/  ",
            apiKey = " key123 "
        ).validate()
        assertIs<ValidationResult.Valid>(result)
        assertEquals(ServerConfig.SelfHosted("https://grocy.casa", "key123"), result.config)
    }

    @Test
    fun `valid hass form builds a home assistant config`() {
        val result = ServerSetupForm(
            usingHass = true,
            serverUrl = "http://ha.local:8123",
            apiKey = "key",
            haToken = "lltoken",
            ingressProxyId = "proxy-id"
        ).validate()
        assertIs<ValidationResult.Valid>(result)
        assertEquals(
            ServerConfig.HomeAssistant("http://ha.local:8123", "proxy-id", "lltoken", "key"),
            result.config
        )
    }

    @Test
    fun `empty url and api key are rejected`() {
        val result = ServerSetupForm().validate()
        assertIs<ValidationResult.Invalid>(result)
        assertNotNull(result.serverUrlError)
        assertNotNull(result.apiKeyError)
        assertNull(result.haTokenError) // hass fields not required in self-hosted mode
    }

    @Test
    fun `non http url is rejected`() {
        val result = ServerSetupForm(serverUrl = "grocy.casa", apiKey = "k").validate()
        assertIs<ValidationResult.Invalid>(result)
        assertNotNull(result.serverUrlError)
    }

    @Test
    fun `hass mode requires token and proxy id`() {
        val result = ServerSetupForm(
            usingHass = true, serverUrl = "http://ha.local:8123", apiKey = "k"
        ).validate()
        assertIs<ValidationResult.Invalid>(result)
        assertNotNull(result.haTokenError)
        assertNotNull(result.proxyIdError)
        assertNull(result.serverUrlError)
    }

    @Test
    fun `login failures map to spanish messages with detail`() {
        assertEquals(
            "La clave API no es válida",
            LoginFailure.InvalidApiKey().toLoginMessage().message
        )
        val unreachable = LoginFailure.UnreachableServer("ConnectException: refused")
            .toLoginMessage()
        assertEquals("No se puede conectar con el servidor", unreachable.message)
        assertEquals("ConnectException: refused", unreachable.detail)
        assertNotNull(RuntimeException("boom").toLoginMessage().message)
    }
}
