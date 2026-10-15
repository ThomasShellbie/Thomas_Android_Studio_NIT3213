package com.s8113459.assignment2.ui.dashboard

import com.s8113459.assignment2.domain.model.DataError
import com.s8113459.assignment2.domain.model.Entity

sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Success(
        val entities: List<Entity>,
        val entityTotal: Int,
        val isRefreshing: Boolean = false,
    ) : DashboardUiState

    data class Error(val error: DataError) : DashboardUiState
}
