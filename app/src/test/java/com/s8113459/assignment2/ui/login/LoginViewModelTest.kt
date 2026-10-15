package com.s8113459.assignment2.ui.login

import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.model.DataError
import com.s8113459.assignment2.domain.repository.AuthRepository
import com.s8113459.assignment2.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `initial state is idle with no errors`() {
        assertEquals(LoginUiState(), viewModel.uiState.value)
    }

    @Test
    fun `empty fields show validation errors and do not call the API`() {
        viewModel.login("", "  ")

        val state = viewModel.uiState.value
        assertEquals(InputError.USERNAME_EMPTY, state.usernameError)
        assertEquals(InputError.PASSWORD_EMPTY, state.passwordError)
        assertFalse(state.isLoading)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `non numeric student id shows validation error`() {
        viewModel.login("abc123", "John")

        assertEquals(InputError.USERNAME_NOT_NUMERIC, viewModel.uiState.value.usernameError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `successful login emits navigation event with keypass`() = runTest {
        coEvery { authRepository.login("12345678", "John") } returns AppResult.Success("art")

        viewModel.login("12345678", "John")

        assertEquals(LoginEvent.NavigateToDashboard("art"), viewModel.events.first())
        assertEquals(LoginUiState(), viewModel.uiState.value)
    }

    @Test
    fun `leading s and whitespace are removed before calling the API`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns AppResult.Success("art")

        viewModel.login("  s12345678 ", " John ")

        coVerify { authRepository.login("12345678", "John") }
    }

    @Test
    fun `password case is preserved`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns AppResult.Success("art")

        viewModel.login("12345678", "jOhN")

        coVerify { authRepository.login("12345678", "jOhN") }
    }

    @Test
    fun `invalid credentials error is exposed in state`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns AppResult.Failure(DataError.InvalidCredentials)

        viewModel.login("12345678", "Wrong")

        val state = viewModel.uiState.value
        assertEquals(DataError.InvalidCredentials, state.loginError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `network error is exposed in state`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns AppResult.Failure(DataError.Network)

        viewModel.login("12345678", "John")

        assertEquals(DataError.Network, viewModel.uiState.value.loginError)
    }

    @Test
    fun `state is loading while the request is in flight and double taps are ignored`() = runTest {
        val pending = CompletableDeferred<AppResult<String>>()
        coEvery { authRepository.login(any(), any()) } coAnswers { pending.await() }

        viewModel.login("12345678", "John")
        assertTrue(viewModel.uiState.value.isLoading)

        viewModel.login("12345678", "John") // second tap while loading
        coVerify(exactly = 1) { authRepository.login(any(), any()) }

        pending.complete(AppResult.Success("art"))
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `editing a field clears previous errors`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns AppResult.Failure(DataError.InvalidCredentials)
        viewModel.login("12345678", "Wrong")

        viewModel.onInputChanged()

        assertNull(viewModel.uiState.value.loginError)
    }
}
