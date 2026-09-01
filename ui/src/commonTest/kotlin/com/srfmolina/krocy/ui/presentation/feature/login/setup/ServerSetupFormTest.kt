package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.GrocyQrCredentials
import com.srfmolina.krocy.domain.model.server.LoginFailure
import com.srfmolina.krocy.domain.model.server.ServerConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
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
            serverUrl = "  http://ha.local:8123/  ",
            apiKey = " key ",
            haToken = " lltoken ",
            ingressProxyId = " proxy-id "
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
        assertEquals("Campo obligatorio", result.serverUrlError)
        assertEquals("Campo obligatorio", result.apiKeyError)
        assertNull(result.haTokenError) // hass fields not required in self-hosted mode
        assertNull(result.proxyIdError) // hass fields not required in self-hosted mode
    }

    @Test
    fun `uppercase url scheme is accepted and normalized to lowercase`() {
        val result = ServerSetupForm(
            serverUrl = "HTTPS://grocy.casa",
            apiKey = "key123"
        ).validate()
        assertIs<ValidationResult.Valid>(result)
        assertEquals(ServerConfig.SelfHosted("https://grocy.casa", "key123"), result.config)
    }

    @Test
    fun `non http url is rejected`() {
        val result = ServerSetupForm(serverUrl = "grocy.casa", apiKey = "k").validate()
        assertIs<ValidationResult.Invalid>(result)
        assertEquals("URL no válida: debe empezar por http:// o https://", result.serverUrlError)
    }

    @Test
    fun `hass mode requires token and proxy id`() {
        val result = ServerSetupForm(
            usingHass = true, serverUrl = "http://ha.local:8123", apiKey = "k"
        ).validate()
        assertIs<ValidationResult.Invalid>(result)
        assertEquals("Campo obligatorio", result.haTokenError)
        assertEquals("Campo obligatorio", result.proxyIdError)
        assertNull(result.serverUrlError)
    }

    @Test
    fun `login failures map to spanish messages with detail`() {
        assertEquals(
            "La clave API no es válida",
            LoginFailure.InvalidApiKey().toLoginMessage().message
        )

        val notGrocyInstance = LoginFailure.NotGrocyInstance("body: not json").toLoginMessage()
        assertEquals(
            "No parece una instancia de Grocy. Si usas Home Assistant, activa el modo Home Assistant.",
            notGrocyInstance.message
        )
        assertEquals("body: not json", notGrocyInstance.detail)

        assertEquals(
            "El identificador del proxy ingress parece incorrecto. Debe ser una cadena larga, " +
                "no un nombre corto como \"gs6h7m3o_grocy\".",
            LoginFailure.WrongIngressProxy().toLoginMessage().message
        )

        assertEquals(
            "El token de acceso de Home Assistant no es válido",
            LoginFailure.InvalidHassToken().toLoginMessage().message
        )

        assertEquals(
            "Problema con el certificado del servidor",
            LoginFailure.SslHandshake("handshake failed").toLoginMessage().message
        )

        assertEquals(
            "El servidor no responde (tiempo de espera agotado)",
            LoginFailure.Timeout("SocketTimeoutException").toLoginMessage().message
        )

        val unreachable = LoginFailure.UnreachableServer("ConnectException: refused")
            .toLoginMessage()
        assertEquals("No se puede conectar con el servidor", unreachable.message)
        assertEquals("ConnectException: refused", unreachable.detail)

        assertEquals(
            "Error inesperado",
            LoginFailure.Unexpected("IOException: broken pipe").toLoginMessage().message
        )
    }

    @Test
    fun `unmapped throwable falls back to unexpected error with its own message as detail`() {
        val mapped = RuntimeException("boom").toLoginMessage()
        assertEquals("Error inesperado", mapped.message)
        assertEquals("boom", mapped.detail)
    }

    @Test
    fun `unmapped throwable without a message falls back to its toString as detail`() {
        val throwable = object : Throwable(null as String?) {}
        val mapped = throwable.toLoginMessage()
        assertEquals("Error inesperado", mapped.message)
        assertEquals(throwable.toString(), mapped.detail)
    }

    @Test
    fun `applying a self hosted qr fills the form and leaves hass mode off`() {
        val form = ServerSetupForm(usingHass = true, haToken = "lltoken")
            .applyQr(GrocyQrCredentials.SelfHosted("https://grocy.casa", "key123"))

        assertEquals(false, form.usingHass)
        assertEquals("https://grocy.casa", form.serverUrl)
        assertEquals("key123", form.apiKey)
    }

    @Test
    fun `applying a hass qr switches to hass mode and preserves a typed token`() {
        val form = ServerSetupForm(haToken = "lltoken")
            .applyQr(
                GrocyQrCredentials.HomeAssistant(
                    haServerUrl = "http://ha.local:8123",
                    ingressProxyId = "proxy-id",
                    apiKey = "key123"
                )
            )

        assertEquals(true, form.usingHass)
        assertEquals("http://ha.local:8123", form.serverUrl)
        assertEquals("proxy-id", form.ingressProxyId)
        assertEquals("key123", form.apiKey)
        assertEquals("lltoken", form.haToken) // the QR never carries it; never clobber it
    }
}
