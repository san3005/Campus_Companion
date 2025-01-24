package com.maggie.rapidsync.repo

import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.User
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getUser(id: String): Response<User> {
        return apiService.getUser(id)
    }

    suspend fun updateUser(id: String, user: User): Response<User> {
        return apiService.updateUser(id, user)
    }

}