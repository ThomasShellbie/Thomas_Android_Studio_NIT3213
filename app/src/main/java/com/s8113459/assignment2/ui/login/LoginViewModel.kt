package com.s8113459.assignment2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Validates the login form and authenticates through [AuthRepository] (injected by Hilt). */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    fun login(rawUsername: String, rawPassword: String) {
        if (_uiState.value.isLoading) return // ignore double taps

        val username = normalizeUsername(rawUsername)
        val password = rawPassword.trim()

        val usernameError = when {
            username.isEmpty() -> InputError.USERNAME_EMPTY
            !username.all(Char::isDigit) -> InputError.USERNAME_NOT_NUMERIC
            else -> null
        }
        val passwordError = if (password.isEmpty()) InputError.PASSWORD_EMPTY else null

        if (usernameError != null || passwordError != null) {
            _uiState.update { it.copy(usernameError = usernameError, passwordError = passwordError, loginError = null) }
            return
        }

        _uiState.value = LoginUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = authRepository.login(username, password)) {
                is AppResult.Success -> {
                    _uiState.value = LoginUiState()
                    _events.send(LoginEvent.NavigateToDashboard(result.data))
                }
                is AppResult.Failure -> _uiState.value = LoginUiState(loginError = result.error)
            }
        }
    }

    /** Clears field errors as soon as the user edits a field. */
    fun onInputChanged() {
        val state = _uiState.value
        if (state.usernameError != null || state.passwordError != null || state.loginError != null) {
            _uiState.update { it.copy(usernameError = null, passwordError = null, loginError = null) }
        }
    }

    companion object {
        /** The API expects the ID without the "s" prefix, so "s12345678" is accepted and normalised to "12345678". */
        fun normalizeUsername(raw: String): String =
            raw.trim().let { if (it.startsWith("s", ignoreCase = true)) it.substring(1) else it }
    }
}
