package com.srfmolina.krocy.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.srfmolina.krocy.data.config.CredentialCipher
import com.srfmolina.krocy.domain.model.server.ServerConfig
import java.nio.file.Files
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Reversible fake so tests can assert what was stored without real crypto. */
private class FakeCipher : CredentialCipher {
    override fun encrypt(plainText: String) = "enc($plainText)"
    override fun decrypt(cipherText: String) =
        cipherText.removePrefix("enc(").removeSuffix(")")
}

/** Simulates a corrupt/tampered store or an invalidated Keystore key: decrypt always fails. */
private class ThrowingDecryptCipher : CredentialCipher {
    override fun encrypt(plainText: String) = plainText
    override fun decrypt(cipherText: String): String =
        throw IllegalStateException("cannot decrypt")
}

class ServerConfigRepositoryImplTest {

    private fun newStore(): DataStore<Preferences> {
        val dir = Files.createTempDirectory("krocy-config").toFile().absolutePath
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = { "$dir/server_config.preferences_pb".toPath() }
        )
    }

    private fun newRepository() = ServerConfigRepositoryImpl(newStore(), FakeCipher())

    @Test
    fun `empty store yields null config`() = runBlocking {
        assertNull(newRepository().get())
    }

    @Test
    fun `round trips demo config`() = runBlocking {
        val repository = newRepository()
        repository.save(ServerConfig.Demo)
        assertEquals(ServerConfig.Demo, repository.get())
    }

    @Test
    fun `round trips self hosted config`() = runBlocking {
        val repository = newRepository()
        val config = ServerConfig.SelfHosted("https://grocy.casa", "key123")
        repository.save(config)
        assertEquals(config, repository.get())
    }

    @Test
    fun `round trips home assistant config`() = runBlocking {
        val repository = newRepository()
        val config = ServerConfig.HomeAssistant(
            haServerUrl = "http://ha.local:8123",
            ingressProxyId = "proxy-id",
            longLivedToken = "lltoken",
            apiKey = "key123"
        )
        repository.save(config)
        assertEquals(config, repository.get())
    }

    @Test
    fun `clear removes config and ingress session`() = runBlocking {
        val repository = newRepository()
        repository.save(ServerConfig.SelfHosted("https://grocy.casa", "key123"))
        repository.save(session = "ingress-abc")
        repository.clear()
        assertNull(repository.get())
        assertNull(repository.session())
    }

    @Test
    fun `ingress session round trips and survives config save`() = runBlocking {
        val repository = newRepository()
        repository.save(session = "ingress-abc")
        repository.save(
            ServerConfig.HomeAssistant("http://ha.local:8123", "p", "t", "k")
        )
        assertEquals("ingress-abc", repository.session())
    }

    @Test
    fun `saving a self hosted config clears a stale ingress session`() = runBlocking {
        val repository = newRepository()
        repository.save(session = "ingress-abc")
        repository.save(ServerConfig.SelfHosted("https://grocy.casa", "key123"))
        assertNull(repository.session())
    }

    @Test
    fun `saving a demo config clears a stale ingress session`() = runBlocking {
        val repository = newRepository()
        repository.save(session = "ingress-abc")
        repository.save(ServerConfig.Demo)
        assertNull(repository.session())
    }

    @Test
    fun `undecryptable store degrades to logged out instead of throwing`() = runBlocking {
        val repository = ServerConfigRepositoryImpl(newStore(), ThrowingDecryptCipher())
        repository.save(ServerConfig.SelfHosted("https://grocy.casa", "key123"))
        repository.save(session = "ingress-abc")

        assertNull(repository.get())
        assertNull(repository.session())
    }

    // --- Cache invalidation regression coverage: read -> mutate -> read, so the first read
    // populates the in-memory cache and the second read must observe the mutation instead of
    // the stale cached value. ---

    @Test
    fun `config cache is invalidated on save`() = runBlocking {
        val repository = newRepository()
        assertNull(repository.get()) // populates cache with null

        repository.save(ServerConfig.Demo)

        assertEquals(ServerConfig.Demo, repository.get())
    }

    @Test
    fun `config cache is invalidated on clear`() = runBlocking {
        val repository = newRepository()
        repository.save(ServerConfig.Demo)
        assertEquals(ServerConfig.Demo, repository.get()) // populates cache

        repository.clear()

        assertNull(repository.get())
    }

    @Test
    fun `session cache is invalidated on session save`() = runBlocking {
        val repository = newRepository()
        assertNull(repository.session()) // populates cache with null

        repository.save(session = "s1")

        assertEquals("s1", repository.session())
    }

    @Test
    fun `session cache is invalidated when switching away from home assistant`() = runBlocking {
        val repository = newRepository()
        repository.save(session = "s1")
        assertEquals("s1", repository.session()) // populates cache

        repository.save(ServerConfig.SelfHosted("https://grocy.casa", "key123"))

        assertNull(repository.session())
    }

    @Test
    fun `session cache is not invalidated by a home assistant save`() = runBlocking {
        val repository = newRepository()
        repository.save(session = "s1")
        assertEquals("s1", repository.session()) // populates cache

        repository.save(
            ServerConfig.HomeAssistant("http://ha.local:8123", "p", "t", "k")
        )

        assertEquals("s1", repository.session())
    }

    @Test
    fun `no stale state survives a logout followed by a login to a different server`() =
        runBlocking {
            val repository = newRepository()
            repository.save(
                ServerConfig.HomeAssistant("http://ha.local:8123", "p", "t", "k")
            )
            repository.save(session = "s1")
            assertEquals(
                ServerConfig.HomeAssistant("http://ha.local:8123", "p", "t", "k"),
                repository.get()
            ) // populates config cache
            assertEquals("s1", repository.session()) // populates session cache

            repository.clear() // logout
            repository.save(ServerConfig.SelfHosted("https://other.casa", "key456")) // login elsewhere

            assertEquals(ServerConfig.SelfHosted("https://other.casa", "key456"), repository.get())
            assertNull(repository.session())
        }
}
