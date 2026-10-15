package com.s8113459.assignment2.data.remote.dto

/** Body of POST /{location}/auth */
data class LoginRequest(
    val username: String,
    val password: String,
)
