package com.maggie.rapidsync.model.pojo

data class LoginResponse(
    val email: String,
    val token: String,
    val user: User
)