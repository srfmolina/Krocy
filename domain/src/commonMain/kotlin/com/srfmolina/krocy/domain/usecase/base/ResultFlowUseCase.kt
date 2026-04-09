package com.srfmolina.krocy.domain.usecase.base

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Base class for use cases that return Flow<Result<R>>.
 *
 * - Catches any upstream exception and emits it as Result.failure.
 * - Explicitly rethrows CancellationException to respect
 *   structured concurrency.
 * - The repository may throw freely; this use case models it.
 */
abstract class ResultFlowUseCase<in P, out R> {

    operator fun invoke(params: P): Flow<Result<R>> =
        execute(params)
            .map<R, Result<R>> { Result.success(it) }
            .catch { e ->
                if (e is CancellationException) throw e  // nunca swallow cancellation
                emit(Result.failure(e))
            }

    protected abstract fun execute(params: P): Flow<R>
}

/**
 * Variant for use cases without input parameters.
 */
abstract class ResultFlowUseCaseNoParams<out R> {

    operator fun invoke(): Flow<Result<R>> =
        execute()
            .map<R, Result<R>> { Result.success(it) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(Result.failure(e))
            }

    protected abstract fun execute(): Flow<R>
}