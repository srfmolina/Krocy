package com.srfmolina.krocy.domain.model.server

/** Result of a successful connection test against a Grocy server. */
data class ServerValidation(
    val grocyVersion: String,
    val isSupported: Boolean
) {
    companion object {
        /** Krocy is developed against the demo server, which runs the Grocy 4 line. */
        const val SUPPORTED_GROCY_MAJOR = 4

        fun forVersion(version: String): ServerValidation = ServerValidation(
            grocyVersion = version,
            isSupported = version.substringBefore('.').toIntOrNull() == SUPPORTED_GROCY_MAJOR
        )
    }
}
