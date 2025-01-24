package com.maggie.rapidsync.model.pojo


data class User(
    val createdDate: String,
    val email: String,
    val fcmToken: String,
    val id: String,
    val password: String,
    val roles: List<Role>,
    val status: String,
    val updatedDate: String,
    val username: String,
    val departmentId: String,
    val profileImageUrl: String,
    val address: String,
    val phone: String,

)