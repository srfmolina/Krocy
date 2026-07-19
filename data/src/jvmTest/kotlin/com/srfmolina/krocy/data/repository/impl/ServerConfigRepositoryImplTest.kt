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
}
