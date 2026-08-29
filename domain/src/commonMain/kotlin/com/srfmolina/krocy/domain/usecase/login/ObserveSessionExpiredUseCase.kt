package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.base.ResultFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow

class ObserveSessionExpiredUseCase(
    private val sessionManager: SessionManager
) : ResultFlowUseCaseNoParams<Unit>() {
    override fun execute(): Flow<Unit> = sessionManager.sessionExpired
}
