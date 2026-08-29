package com.srfmolina.krocy.domain.model.server

/**
 * Failures produced while validating a server or authenticating against it.
 * [detail] carries the exact underlying error (status code, exception text) for
 * the collapsible detail line in the UI.
 */
sealed class LoginFailure(val detail: String? = null) : Exception(detail) {
    class InvalidApiKey : LoginFailure()
    class NotGrocyInstance(detail: String? = null) : LoginFailure(detail)
    class WrongIngressProxy(detail: String? = null) : LoginFailure(detail)
    class InvalidHassToken : LoginFailure()
    class SslHandshake(detail: String) : LoginFailure(detail)
    class UnreachableServer(detail: String) : LoginFailure(detail)
    class Timeout(detail: String) : LoginFailure(detail)
    class Unexpected(detail: String) : LoginFailure(detail)
}
