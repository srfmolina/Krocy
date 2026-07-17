package com.srfmolina.krocy.domain.session

import com.srfmolina.krocy.domain.model.server.ServerConfig
import kotlinx.coroutines.flow.Flow

/**
 * Lifecycle of the server-bound dependency graph (HTTP client, APIs, repositories).
 * [open] must be called before any screen that talks to the server; [close] tears
 * the graph down on logout.
 */
interface SessionManager {
    /** Emits when authentication is irrecoverably lost (e.g. HA re-auth failed). */
    val sessionExpired: Flow<Unit>
    suspend fun open(config: ServerConfig)
    suspend fun close()
}
