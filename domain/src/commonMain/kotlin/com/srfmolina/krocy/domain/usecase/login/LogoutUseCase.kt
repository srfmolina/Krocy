package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class LogoutUseCase(
    private val sessionManager: SessionManager,
    private val serverConfigRepository: ServerConfigRepository,
    private val krocyItemRepository: KrocyItemRepository
) : ResultUseCaseNoParams<Unit>() {
    override suspend fun execute() {
        sessionManager.close()
        serverConfigRepository.clear()
        krocyItemRepository.clearAll()
    }
}
