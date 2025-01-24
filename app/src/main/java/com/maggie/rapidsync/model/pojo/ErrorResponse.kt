package com.maggie.rapidsync.model.pojo

data class ErrorResponse(
    val description: String,
    val message: String,
    val statusCode: Int,
    val timestamp: String
)