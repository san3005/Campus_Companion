package com.maggie.rapidsync.repo

import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.MealPlan
import com.maggie.rapidsync.model.pojo.Restaurant
import com.maggie.rapidsync.model.pojo.StripeConfigRequest
import com.maggie.rapidsync.model.pojo.StripeConfigResponse
import com.maggie.rapidsync.model.pojo.UserMealPlan
import retrofit2.Response
import javax.inject.Inject

class MealPlanRepository @Inject constructor(private val apiService: ApiService) {


    suspend fun getAllRestaurants(): Response<List<Restaurant>> {
        return apiService.getRestaurants()
    }

    suspend fun getAllMealPlans(): Response<List<MealPlan>> {
        return apiService.getMealPlans()
    }

    suspend fun bookMealPlan(userMealPlan: UserMealPlan): Response<UserMealPlan> {
        return apiService.bookMealPlan(userMealPlan)
    }

    suspend fun getUserMealPlan(userId: String): Response<UserMealPlan> {
        return apiService.getUserMealPlans(userId)
    }

    suspend fun cancelMealPlan(id: String): Response<UserMealPlan> {
        return apiService.cancelMealPlan(id)
    }

    suspend fun getStripeClientSecret(stripeConfigRequest: StripeConfigRequest): Response<StripeConfigResponse> {
        return apiService.createPaymentIntent(stripeConfigRequest)
    }
}