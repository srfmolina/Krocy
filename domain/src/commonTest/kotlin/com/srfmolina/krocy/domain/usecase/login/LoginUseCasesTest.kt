package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.model.example.KrocyItem
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginUseCasesTest {

    private class RecordingConfigRepository : ServerConfigRepository {
        val calls = mutableListOf<String>()
        var stored: ServerConfig? = null
        override suspend fun get(): ServerConfig? = stored
        override suspend fun save(config: ServerConfig) { calls += "save"; stored = config }
        override suspend fun clear() { calls += "clear"; stored = null }
    }

    private class RecordingSessionManager(private val log: MutableList<String>) : SessionManager {
        override val sessionExpired: Flow<Unit> = emptyFlow()
        override suspend fun open(config: ServerConfig) { log += "open" }
        override suspend fun close() { log += "close" }
    }

    private class RecordingItemRepository(private val log: MutableList<String>) : KrocyItemRepository {
        override fun getAll(): Flow<List<KrocyItem>> = emptyFlow()
        override suspend fun save(item: KrocyItem) = Unit
        override suspend fun deleteById(id: Int) = Unit
        override suspend fun clearAll() { log += "wipe" }
    }

    @Test
    fun `complete login saves config before opening session`() = runBlocking {
        val configRepo = RecordingConfigRepository()
        val log = configRepo.calls
        val result = CompleteLoginUseCase(configRepo, RecordingSessionManager(log))
            .invoke(ServerConfig.Demo)
        assertTrue(result.isSuccess)
        assertEquals(listOf("save", "open"), log)
        assertEquals(ServerConfig.Demo, configRepo.stored)
    }

    @Test
    fun `logout still clears config and cache when closing the session fails`() = runBlocking {
        val log = mutableListOf<String>()
        val failingSessionManager = object : SessionManager {
            override val sessionExpired: Flow<Unit> = emptyFlow()
            override suspend fun open(config: ServerConfig) = Unit
            override suspend fun close() { log += "close"; error("close failed") }
        }
        val configRepo = object : ServerConfigRepository {
            override suspend fun get(): ServerConfig? = null
            override suspend fun save(config: ServerConfig) = Unit
            override suspend fun clear() { log += "clear" }
        }
        val result = LogoutUseCase(
            failingSessionManager, configRepo, RecordingItemRepository(log)
        ).invoke()
        assertTrue(result.isFailure)
        assertEquals(listOf("close", "clear", "wipe"), log)
    }

    @Test
    fun `logout still wipes the cache when clearing the config fails`() = runBlocking {
        val log = mutableListOf<String>()
        val failingConfigRepo = object : ServerConfigRepository {
            override suspend fun get(): ServerConfig? = null
            override suspend fun save(config: ServerConfig) = Unit
            override suspend fun clear() { log += "clear"; error("clear failed") }
        }
        val result = LogoutUseCase(
            RecordingSessionManager(log), failingConfigRepo, RecordingItemRepository(log)
        ).invoke()
        assertTrue(result.isFailure)
        assertEquals(listOf("close", "clear", "wipe"), log)
    }

    @Test
    fun `logout reports failure when the cache wipe fails`() = runBlocking {
        val log = mutableListOf<String>()
        val configRepo = object : ServerConfigRepository {
            override suspend fun get(): ServerConfig? = null
            override suspend fun save(config: ServerConfig) = Unit
            override suspend fun clear() { log += "clear" }
        }
        val failingItemRepo = object : KrocyItemRepository {
            override fun getAll(): Flow<List<KrocyItem>> = emptyFlow()
            override suspend fun save(item: KrocyItem) = Unit
            override suspend fun deleteById(id: Int) = Unit
            override suspend fun clearAll() { log += "wipe"; error("wipe failed") }
        }
        val result = LogoutUseCase(
            RecordingSessionManager(log), configRepo, failingItemRepo
        ).invoke()
        assertTrue(result.isFailure)
        assertEquals(listOf("close", "clear", "wipe"), log)
    }

    @Test
    fun `logout closes session then clears config and local cache`() = runBlocking {
        val log = mutableListOf<String>()
        val configRepo = object : ServerConfigRepository {
            override suspend fun get(): ServerConfig? = null
            override suspend fun save(config: ServerConfig) = Unit
            override suspend fun clear() { log += "clear" }
        }
        val result = LogoutUseCase(
            RecordingSessionManager(log), configRepo, RecordingItemRepository(log)
        ).invoke()
        assertTrue(result.isSuccess)
        assertEquals(listOf("close", "clear", "wipe"), log)
    }
}
