package com.srfmolina.krocy.ui.presentation.feature.login.setup

import com.srfmolina.krocy.domain.model.server.LoginFailure

internal data class LoginMessage(val message: String, val detail: String?)

/** Spanish user-facing message + exact detail, mirroring grocy-android's error mapping. */
internal fun Throwable.toLoginMessage(): LoginMessage = when (this) {
    is LoginFailure.InvalidApiKey ->
        LoginMessage("La clave API no es válida", detail)
    is LoginFailure.NotGrocyInstance -> LoginMessage(
        "No parece una instancia de Grocy. Si usas Home Assistant, activa el modo Home Assistant.",
        detail
    )
    is LoginFailure.WrongIngressProxy -> LoginMessage(
        "El identificador del proxy ingress parece incorrecto. Debe ser una cadena larga, " +
            "no un nombre corto como \"gs6h7m3o_grocy\".",
        detail
    )
    is LoginFailure.InvalidHassToken ->
        LoginMessage("El token de acceso de Home Assistant no es válido", detail)
    is LoginFailure.SslHandshake ->
        LoginMessage("Problema con el certificado del servidor", detail)
    is LoginFailure.Timeout ->
        LoginMessage("El servidor no responde (tiempo de espera agotado)", detail)
    is LoginFailure.UnreachableServer ->
        LoginMessage("No se puede conectar con el servidor", detail)
    is LoginFailure.Unexpected ->
        LoginMessage("Error inesperado", detail)
    else -> LoginMessage("Error inesperado", message ?: toString())
}
