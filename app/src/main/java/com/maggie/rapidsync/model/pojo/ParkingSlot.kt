package com.maggie.rapidsync.model.pojo

data class ParkingSlot(
    val available: Boolean,
    val id: String,
    val location: String,
    val speciality: String,
    val user: User?
)