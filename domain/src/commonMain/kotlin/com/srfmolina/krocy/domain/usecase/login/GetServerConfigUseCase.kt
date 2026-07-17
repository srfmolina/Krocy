package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class GetServerConfigUseCase(
    private val serverConfigRepository: ServerConfigRepository
) : ResultUseCaseNoParams<ServerConfig?>() {
    override suspend fun execute(): ServerConfig? = serverConfigRepository.get()
}
