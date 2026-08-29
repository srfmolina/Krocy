package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.LoginFailure
import kotlin.test.Test
import kotlin.test.assertEquals

/** Pins the exact Spanish strings so the mapping can't rot silently. */
class LoginErrorMessagesTest {

    @Test
    fun `every failure maps to its exact user-facing message`() {
        val cases = listOf<Pair<Throwable, String>>(
            LoginFailure.InvalidApiKey() to
                "La clave API no es válida",
            LoginFailure.NotGrocyInstance("HTTP 404") to
                "No parece una instancia de Grocy. Si usas Home Assistant, activa el modo Home Assistant.",
            LoginFailure.WrongIngressProxy("HTTP 503") to
                "El identificador del proxy ingress parece incorrecto. Debe ser una cadena larga, " +
                "no un nombre corto como \"gs6h7m3o_grocy\".",
            LoginFailure.InvalidHassToken() to
                "El token de acceso de Home Assistant no es válido",
            LoginFailure.SslHandshake("cert expired") to
                "Problema con el certificado del servidor",
            LoginFailure.Timeout("15s") to
                "El servidor no responde (tiempo de espera agotado)",
            LoginFailure.UnreachableServer("refused") to
                "No se puede conectar con el servidor",
            LoginFailure.Unexpected("boom") to
                "Error inesperado"
        )
        for ((failure, expected) in cases) {
            assertEquals(expected, failure.toLoginMessage().message, "for $failure")
        }
    }

    @Test
    fun `failures carry their detail through to the collapsible line`() {
        assertEquals("HTTP 404", LoginFailure.NotGrocyInstance("HTTP 404").toLoginMessage().detail)
        assertEquals("cert expired", LoginFailure.SslHandshake("cert expired").toLoginMessage().detail)
    }

    @Test
    fun `a leaked non-LoginFailure exception hits the safety net`() {
        val leaked = IllegalStateException("mapper bug")
        val message = leaked.toLoginMessage()
        assertEquals("Error inesperado", message.message)
        assertEquals("mapper bug", message.detail)
    }
}
