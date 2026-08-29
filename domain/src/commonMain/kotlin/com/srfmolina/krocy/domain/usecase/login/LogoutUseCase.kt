package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams
import kotlinx.coroutines.CancellationException

class LogoutUseCase(
    private val sessionManager: SessionManager,
    private val serverConfigRepository: ServerConfigRepository,
    private val krocyItemRepository: KrocyItemRepository
) : ResultUseCaseNoParams<Unit>() {
    override suspend fun execute() {
        // The three teardown steps are independent: a failing one must not leave the
        // later ones undone (e.g. credentials surviving on disk behind a logged-out UI).
        val results = listOf(
            runCatchingTeardown { sessionManager.close() },
            runCatchingTeardown { serverConfigRepository.clear() },
            runCatchingTeardown { krocyItemRepository.clearAll() }
        )
        results.firstOrNull { it.isFailure }?.getOrThrow()
    }

    private inline fun <T> runCatchingTeardown(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e)
        }
}
