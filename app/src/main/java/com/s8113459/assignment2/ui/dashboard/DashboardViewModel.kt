package com.s8113459.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Loads the entities for the keypass received from the login screen.
 * The keypass arrives through [SavedStateHandle] (populated from the Activity's intent extras),
 * so it also survives process death.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository,
) : ViewModel() {

    val keypass: String = requireNotNull(savedStateHandle.get<String>(KEY_KEYPASS)) {
        "DashboardViewModel requires a '$KEY_KEYPASS' argument"
    }

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    /** Reloads the data. Keeps the current list on screen while refreshing (pull-to-refresh). */
    fun refresh() {
        val current = _uiState.value
        if (current is DashboardUiState.Success) {
            if (current.isRefreshing) return
            _uiState.value = current.copy(isRefreshing = true)
        } else {
            _uiState.value = DashboardUiState.Loading
        }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = when (val result = dashboardRepository.getDashboard(keypass)) {
                is AppResult.Success -> DashboardUiState.Success(
                    entities = result.data.entities,
                    entityTotal = result.data.entityTotal,
                )
                is AppResult.Failure -> DashboardUiState.Error(result.error)
            }
        }
    }

    companion object {
        const val KEY_KEYPASS = "keypass"
    }
}
