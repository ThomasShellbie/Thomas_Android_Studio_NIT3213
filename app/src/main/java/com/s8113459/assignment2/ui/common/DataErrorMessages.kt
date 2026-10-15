package com.s8113459.assignment2.ui.common

import androidx.annotation.StringRes
import com.s8113459.assignment2.R
import com.s8113459.assignment2.domain.model.DataError

/** Maps a [DataError] to a user-facing message for the login screen. */
@StringRes
fun DataError.loginMessage(): Int = when (this) {
    DataError.InvalidCredentials -> R.string.error_invalid_credentials
    DataError.NotFound -> R.string.error_student_not_found
    DataError.Network -> R.string.error_network
    is DataError.Server -> R.string.error_server
    DataError.Unknown -> R.string.error_unknown
}

/** Maps a [DataError] to a user-facing message for the dashboard screen. */
@StringRes
fun DataError.dashboardMessage(): Int = when (this) {
    DataError.InvalidCredentials, DataError.NotFound -> R.string.error_invalid_keypass
    DataError.Network -> R.string.error_network
    is DataError.Server -> R.string.error_server
    DataError.Unknown -> R.string.error_unknown
}
