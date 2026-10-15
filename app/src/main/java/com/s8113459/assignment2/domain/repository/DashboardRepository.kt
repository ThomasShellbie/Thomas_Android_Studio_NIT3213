package com.s8113459.assignment2.domain.repository

import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.model.Dashboard

/** Loads the list of entities for a keypass. */
interface DashboardRepository {
    suspend fun getDashboard(keypass: String): AppResult<Dashboard>
}
