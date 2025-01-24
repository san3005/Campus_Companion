package com.maggie.rapidsync.repo

import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.UniversityEvent
import retrofit2.Response
import javax.inject.Inject

class UniversityEventRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAll(): Response<List<UniversityEvent>> {
        return apiService.getUniversityEvents()
    }


}