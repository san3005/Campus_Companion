package com.maggie.rapidsync.repo

import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.ParkingPlan
import com.maggie.rapidsync.model.pojo.StripeConfigRequest
import com.maggie.rapidsync.model.pojo.StripeConfigResponse
import com.maggie.rapidsync.model.pojo.UserParkingPlan
import retrofit2.Response
import javax.inject.Inject

class ParkingPlanRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAll(): Response<List<ParkingPlan>> {
        return apiService.getParkingPlans()
    }

    suspend fun bookParkingPlan(userParkingPlan: UserParkingPlan): Response<UserParkingPlan> {
        return apiService.bookParkingPlan(userParkingPlan)
    }

    suspend fun getUserParkingPlans(userId: String): Response<UserParkingPlan> {
        return apiService.getUserParkingPlans(userId)
    }

    suspend fun cancelParkingPlan(userParkingPlan: UserParkingPlan): Response<UserParkingPlan> {
        return apiService.cancelParkingPlan(userParkingPlan.id!!)
    }

    suspend fun getStripeClientSecret(stripeConfigRequest: StripeConfigRequest): Response<StripeConfigResponse> {
        return apiService.createPaymentIntent(stripeConfigRequest)
    }

}