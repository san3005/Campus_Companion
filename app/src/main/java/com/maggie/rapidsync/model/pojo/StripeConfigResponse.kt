package com.maggie.rapidsync.model.pojo

data class StripeConfigResponse(
    val customerId: String,
    val ephemeralKeySecret: String,
    val paymentIntentId: String,
    val clientSecret: String
)