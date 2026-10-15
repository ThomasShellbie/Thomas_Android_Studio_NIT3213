package com.s8113459.assignment2.domain.model

/**
 * Errors the data layer can report. The UI maps each one to a user-friendly message,
 * so ViewModels never deal with HTTP codes or exceptions directly.
 */
sealed interface DataError {
    /** Server rejected the username / password (HTTP 400, 401 or 403). */
    data object InvalidCredentials : DataError

    /** Resource does not exist (HTTP 404): unknown student ID, wrong campus or invalid keypass. */
    data object NotFound : DataError

    /** No connection, DNS failure or timeout. */
    data object Network : DataError

    /** Server-side problem (HTTP 5xx). */
    data class Server(val code: Int) : DataError

    /** Anything else (unexpected status code, malformed JSON...). */
    data object Unknown : DataError
}
