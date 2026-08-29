package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class OpenSessionUseCase(
    private val sessionManager: SessionManager
) : ResultUseCase<ServerConfig, Unit>() {
    override suspend fun execute(params: ServerConfig) = sessionManager.open(params)
}
