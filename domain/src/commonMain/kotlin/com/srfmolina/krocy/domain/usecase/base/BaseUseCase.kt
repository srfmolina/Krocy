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

/**
 * Base class for one-shot (suspend) use cases that return Result<R>.
 *
 * - Wraps a successful result in Result.success.
 * - Explicitly rethrows CancellationException to respect
 *   structured concurrency.
 * - Any other throwable is emitted as Result.failure.
 * - The repository may throw freely; this use case models it.
 */
abstract class ResultUseCase<in P, out R> {

    suspend operator fun invoke(params: P): Result<R> =
        try {
            Result.success(execute(params))
        } catch (e: CancellationException) {
            throw e // nunca swallow cancellation
        } catch (e: Throwable) {
            Result.failure(e)
        }

    protected abstract suspend fun execute(params: P): R
}