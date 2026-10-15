package com.s8113459.assignment2.data.remote.dto

/** Response of POST /{location}/auth — nullable because Gson does not enforce Kotlin null-safety. */
data class LoginResponse(
    val keypass: String?,
)
