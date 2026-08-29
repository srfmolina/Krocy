package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.model.server.ServerValidation
import com.srfmolina.krocy.domain.repository.LoginRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class ValidateServerUseCase(
    private val loginRepository: LoginRepository
) : ResultUseCase<ServerConfig, ServerValidation>() {
    override suspend fun execute(params: ServerConfig): ServerValidation =
        loginRepository.validate(params)
}
