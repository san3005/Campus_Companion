package com.maggie.rapidsync.model.pojo

data class University(
    val address: String,
    val city: String,
    val country: String,
    val description: String,
    val email: String,
    val id: String,
    val images: List<String>,
    val location: String,
    val logo: String,
    val name: String,
    val phone: String,
    val state: String,
    val website: String,
    val zip: String
)