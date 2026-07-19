package com.srfmolina.krocy.data.session

import com.srfmolina.krocy.data.di.sessionModule
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.session.SessionManager
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

internal class SessionManagerImpl : SessionManager, KoinComponent {

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val sessionExpired: Flow<Unit> = _sessionExpired

    private val mutex = Mutex()
    private var loaded: Module? = null

    override suspend fun open(config: ServerConfig) = mutex.withLock {
        unloadLocked()
        val module = sessionModule(config, onSessionExpired = { _sessionExpired.tryEmit(Unit) })
        loadKoinModules(module)
        loaded = module
    }

    override suspend fun close() = mutex.withLock { unloadLocked() }

    private fun unloadLocked() {
        val module = loaded ?: return
        // Close the shared client before dropping its definitions.
        runCatching { getKoin().getOrNull<HttpClient>()?.close() }
        unloadKoinModules(module)
        loaded = null
    }
}
