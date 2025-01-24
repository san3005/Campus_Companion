package com.maggie.rapidsync.repo

import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.LoginRequest
import com.maggie.rapidsync.model.pojo.LoginResponse
import com.maggie.rapidsync.model.pojo.SaveFcmRequest
import com.maggie.rapidsync.model.pojo.User
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return apiService.login(loginRequest)
    }

    suspend fun saveToken(fcmRequest: SaveFcmRequest): Response<User> {
        // write code to save token
        return apiService.saveFcmToken(fcmRequest)
    }
}