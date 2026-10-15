package com.s8113459.assignment2.data.repository

import com.s8113459.assignment2.data.mapper.EntityMapper
import com.s8113459.assignment2.data.remote.ApiService
import com.s8113459.assignment2.di.IoDispatcher
import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.model.Dashboard
import com.s8113459.assignment2.domain.repository.DashboardRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/** [DashboardRepository] backed by the REST API. */
class DashboardRepositoryImpl @Inject constructor(
    private val api: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : DashboardRepository {

    override suspend fun getDashboard(keypass: String): AppResult<Dashboard> =
        safeApiCall(ioDispatcher) {
            EntityMapper.toDashboard(api.getDashboard(keypass))
        }
}
