package com.s8113459.assignment2.data.remote

import com.s8113459.assignment2.data.remote.dto.DashboardResponse
import com.s8113459.assignment2.data.remote.dto.LoginRequest
import com.s8113459.assignment2.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Retrofit definition of the nit3213api endpoints. */
interface ApiService {

    /** @param location class location: "footscray", "sydney" or "br" */
    @POST("{location}/auth")
    suspend fun login(
        @Path("location") location: String,
        @Body request: LoginRequest,
    ): LoginResponse

    @GET("dashboard/{keypass}")
    suspend fun getDashboard(
        @Path("keypass") keypass: String,
    ): DashboardResponse
}
