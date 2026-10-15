package com.s8113459.assignment2.domain.repository

import com.s8113459.assignment2.domain.model.AppResult

/** Authenticates a student against the NIT3213 API. */
interface AuthRepository {
    /**
     * @param username student ID without the leading "s", e.g. "12345678"
     * @param password student's first name (case sensitive)
     * @return the keypass (topic name) used to load the dashboard
     */
    suspend fun login(username: String, password: String): AppResult<String>
}
