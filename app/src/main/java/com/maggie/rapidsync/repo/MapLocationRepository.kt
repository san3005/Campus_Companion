package com.maggie.rapidsync.repo

import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.MapLocation
import com.maggie.rapidsync.model.pojo.MealPlan
import com.maggie.rapidsync.model.pojo.Restaurant
import com.maggie.rapidsync.model.pojo.UserMealPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class MapLocationRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllLocations(): Flow<NetworkResult<List<MapLocation>>> = flow {
        try {
            val response = apiService.getLocations()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

}