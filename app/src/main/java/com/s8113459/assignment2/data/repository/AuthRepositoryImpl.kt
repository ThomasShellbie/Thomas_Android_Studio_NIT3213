package com.s8113459.assignment2.data.repository

import com.s8113459.assignment2.data.remote.ApiService
import com.s8113459.assignment2.data.remote.dto.LoginRequest
import com.s8113459.assignment2.di.AuthLocation
import com.s8113459.assignment2.di.IoDispatcher
import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.model.DataError
import com.s8113459.assignment2.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/** [AuthRepository] backed by the REST API. All collaborators are provided by Hilt. */
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    @AuthLocation private val location: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun login(username: String, password: String): AppResult<String> {
        val result = safeApiCall(ioDispatcher) {
            api.login(location, LoginRequest(username = username, password = password))
        }
        return when (result) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                val keypass = result.data.keypass
                if (keypass.isNullOrBlank()) AppResult.Failure(DataError.Unknown) else AppResult.Success(keypass)
            }
        }
    }
}
