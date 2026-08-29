package com.srfmolina.krocy.data.config

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val GCM_IV_BYTES = 12
private const val GCM_TAG_BITS = 128

internal class JvmCredentialCipher(private val keyFile: File) : CredentialCipher {

    private val key: SecretKeySpec by lazy { SecretKeySpec(loadOrCreateKey(), "AES") }

    private fun loadOrCreateKey(): ByteArray {
        if (keyFile.exists()) return keyFile.readBytes()
        keyFile.parentFile?.let(::createPrivateDirectory)
        // Restrictive permissions must be in place BEFORE any key material touches disk;
        // chmod-after-write leaves the raw key world-readable during the window (and
        // forever, if the chmod silently fails).
        createOwnerOnly(keyFile)
        val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        keyFile.writeBytes(bytes)
        return bytes
    }

    private fun createPrivateDirectory(dir: File) {
        if (dir.exists()) return
        val ownerOnly = PosixFilePermissions.asFileAttribute(
            setOf(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE
            )
        )
        try {
            Files.createDirectories(dir.toPath(), ownerOnly)
        } catch (_: UnsupportedOperationException) {
            Files.createDirectories(dir.toPath()) // non-POSIX (e.g. NTFS): default ACLs
        }
    }

    private fun createOwnerOnly(file: File) {
        val ownerOnly = PosixFilePermissions.asFileAttribute(
            setOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE)
        )
        try {
            Files.createFile(file.toPath(), ownerOnly)
        } catch (_: UnsupportedOperationException) {
            Files.createFile(file.toPath()) // non-POSIX (e.g. NTFS): default ACLs
        }
    }

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(GCM_IV_BYTES).also { SecureRandom().nextBytes(it) }
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        return Base64.getEncoder().encodeToString(iv + cipher.doFinal(plainText.encodeToByteArray()))
    }

    override fun decrypt(cipherText: String): String {
        val all = Base64.getDecoder().decode(cipherText)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, all, 0, GCM_IV_BYTES))
        return cipher.doFinal(all, GCM_IV_BYTES, all.size - GCM_IV_BYTES).decodeToString()
    }
}

internal actual fun createCredentialCipher(): CredentialCipher =
    JvmCredentialCipher(File(System.getProperty("user.home"), ".krocy/credentials.key"))
