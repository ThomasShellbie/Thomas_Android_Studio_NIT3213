package com.s8113459.assignment2.domain.model

/** Result of a repository call: either data or a [DataError]. */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: DataError) : AppResult<Nothing>
}
