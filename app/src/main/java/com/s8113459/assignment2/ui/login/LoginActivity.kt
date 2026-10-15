package com.s8113459.assignment2.ui.login

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.s8113459.assignment2.BuildConfig
import com.s8113459.assignment2.R
import com.s8113459.assignment2.databinding.ActivityLoginBinding
import com.s8113459.assignment2.ui.common.hideKeyboard
import com.s8113459.assignment2.ui.common.loginMessage
import com.s8113459.assignment2.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** Screen 1 — login with student ID (username) and first name (password). */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chipCampus.text = getString(
            R.string.campus_label,
            BuildConfig.AUTH_LOCATION.replaceFirstChar { it.uppercaseChar() },
        )

        binding.btnLogin.setOnClickListener { submit() }
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { submit(); true } else false
        }
        binding.etUsername.doAfterTextChanged { viewModel.onInputChanged() }
        binding.etPassword.doAfterTextChanged { viewModel.onInputChanged() }

        observeViewModel()
    }

    private fun submit() {
        binding.root.hideKeyboard()
        viewModel.login(
            rawUsername = binding.etUsername.text?.toString().orEmpty(),
            rawPassword = binding.etPassword.text?.toString().orEmpty(),
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect(::render) }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is LoginEvent.NavigateToDashboard -> {
                                startActivity(DashboardActivity.newIntent(this@LoginActivity, event.keypass))
                                finish() // Back from the dashboard should not return to the login form
                            }
                        }
                    }
                }
            }
        }
    }

    private fun render(state: LoginUiState) {
        binding.apply {
            tilUsername.error = when (state.usernameError) {
                InputError.USERNAME_EMPTY -> getString(R.string.error_username_empty)
                InputError.USERNAME_NOT_NUMERIC -> getString(R.string.error_username_numeric)
                else -> null
            }
            tilPassword.error = if (state.passwordError == InputError.PASSWORD_EMPTY) {
                getString(R.string.error_password_empty)
            } else null

            val loginError = state.loginError
            tvLoginError.visibility = if (loginError != null) View.VISIBLE else View.GONE
            if (loginError != null) tvLoginError.setText(loginError.loginMessage())

            progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !state.isLoading
            btnLogin.text = getString(if (state.isLoading) R.string.signing_in else R.string.sign_in)
            tvSlowHint.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            tilUsername.isEnabled = !state.isLoading
            tilPassword.isEnabled = !state.isLoading
        }
    }
}
