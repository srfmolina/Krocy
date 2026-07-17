package com.srfmolina.krocy.data.config

import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class JvmCredentialCipherTest {

    private fun tempKeyFile(): File =
        File(Files.createTempDirectory("krocy-cipher").toFile(), "credentials.key")

    @Test
    fun `round trips a secret`() {
        val cipher = JvmCredentialCipher(tempKeyFile())
        val encrypted = cipher.encrypt("super-secret-api-key")
        assertNotEquals("super-secret-api-key", encrypted)
        assertEquals("super-secret-api-key", cipher.decrypt(encrypted))
    }

    @Test
    fun `same plaintext encrypts differently each time`() {
        val cipher = JvmCredentialCipher(tempKeyFile())
        assertNotEquals(cipher.encrypt("x"), cipher.encrypt("x"))
    }

    @Test
    fun `reuses the key across instances`() {
        val keyFile = tempKeyFile()
        val encrypted = JvmCredentialCipher(keyFile).encrypt("persisted")
        assertEquals("persisted", JvmCredentialCipher(keyFile).decrypt(encrypted))
    }

    @Test
    fun `key file is owner-only where posix permissions exist`() {
        val keyFile = tempKeyFile()
        JvmCredentialCipher(keyFile).encrypt("x")
        assertTrue(keyFile.exists())
        val posix = runCatching {
            Files.getPosixFilePermissions(keyFile.toPath())
        }.getOrNull() ?: return // non-POSIX filesystem: nothing to assert
        assertEquals("[OWNER_READ, OWNER_WRITE]", posix.sorted().toString())
    }
}
