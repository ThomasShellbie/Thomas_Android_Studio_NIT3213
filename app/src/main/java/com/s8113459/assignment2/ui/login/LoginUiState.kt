package com.s8113459.assignment2.ui.login

import com.s8113459.assignment2.domain.model.DataError

/** Everything the login screen needs to render itself. */
data class LoginUiState(
    val isLoading: Boolean = false,
    val usernameError: InputError? = null,
    val passwordError: InputError? = null,
    val loginError: DataError? = null,
)

/** Client-side validation errors (checked before calling the API). */
enum class InputError {
    USERNAME_EMPTY,
    USERNAME_NOT_NUMERIC,
    PASSWORD_EMPTY,
}

/** One-off events that must be handled exactly once (e.g. navigation). */
sealed interface LoginEvent {
    data class NavigateToDashboard(val keypass: String) : LoginEvent
}
