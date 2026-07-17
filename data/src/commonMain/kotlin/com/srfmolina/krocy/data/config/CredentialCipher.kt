package com.srfmolina.krocy.data.config

/**
 * Encrypts credential values before they reach DataStore. Android backs the key
 * with the Android Keystore; JVM keeps an owner-only key file (gh/kubectl model).
 */
internal interface CredentialCipher {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String
}

internal expect fun createCredentialCipher(): CredentialCipher
