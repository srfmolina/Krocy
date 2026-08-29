package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.server.ServerConfig

interface ServerConfigRepository {
    suspend fun get(): ServerConfig?
    suspend fun save(config: ServerConfig)
    suspend fun clear()
}
