package com.maggie.rapidsync.model.pojo

data class StripeConfigRequest(
    val amount: String,
    val userId: String
)