package com.s8113459.assignment2.data.repository

import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.model.DataError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Runs a network call on [dispatcher] and converts exceptions into [DataError]s,
 * so every repository handles failures the same way.
 */
internal suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    call: suspend () -> T,
): AppResult<T> = withContext(dispatcher) {
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e // never swallow coroutine cancellation
    } catch (e: HttpException) {
        AppResult.Failure(httpCodeToError(e.code()))
    } catch (e: MalformedJsonException) {
        AppResult.Failure(DataError.Unknown) // bad JSON is not a connectivity problem
    } catch (e: IOException) {
        AppResult.Failure(DataError.Network)
    } catch (e: Exception) {
        AppResult.Failure(DataError.Unknown)
    }
}

internal fun httpCodeToError(code: Int): DataError = when (code) {
    400, 401, 403 -> DataError.InvalidCredentials
    404 -> DataError.NotFound
    in 500..599 -> DataError.Server(code)
    else -> DataError.Unknown
}
