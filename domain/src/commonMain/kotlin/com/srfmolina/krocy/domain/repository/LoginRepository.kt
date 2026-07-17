package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.model.server.ServerValidation

interface LoginRepository {
    /**
     * Tests connectivity and authentication against [config] without persisting anything.
     * For Home Assistant configs this first acquires (and stores) an ingress session.
     * Throws [com.srfmolina.krocy.domain.model.server.LoginFailure] on any failure.
     */
    suspend fun validate(config: ServerConfig): ServerValidation
}
