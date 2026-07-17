package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.server.ServerConfig
import kotlinx.coroutines.flow.Flow

interface ServerConfigRepository {
    val config: Flow<ServerConfig?>
    suspend fun get(): ServerConfig?
    suspend fun save(config: ServerConfig)
    suspend fun clear()
}
