package com.maggie.rapidsync.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.MealPlan
import com.maggie.rapidsync.model.pojo.StripeConfigRequest
import com.maggie.rapidsync.model.pojo.StripeConfigResponse
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.model.pojo.UserMealPlan
import com.maggie.rapidsync.repo.MealPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {


    private lateinit var user: User

    init {
        viewModelScope.launch {
            user = localDataStore.getObject(Constants.USER, object : TypeToken<User>() {})!!
        }

    }

    private lateinit var userMealPlanLocal: UserMealPlan

    private val _mealPlans =
        MutableStateFlow<NetworkResult<List<MealPlan>>>(NetworkResult.Initial)
    val mealPlans: StateFlow<NetworkResult<List<MealPlan>>> = _mealPlans.asStateFlow()


    private val _userMealPlan =
        MutableStateFlow<NetworkResult<UserMealPlan>>(NetworkResult.Initial)
    val userMealPlan: StateFlow<NetworkResult<UserMealPlan>> = _userMealPlan.asStateFlow()

    private val _stripeConfigResponse =
        MutableStateFlow<NetworkResult<StripeConfigResponse>>(NetworkResult.Initial)
    val stripeConfigResponse: StateFlow<NetworkResult<StripeConfigResponse>> =
        _stripeConfigResponse.asStateFlow()


    fun fetchUserMealPlan() {
        viewModelScope.launch {
            _userMealPlan.value = NetworkResult.Loading
            try {
                val response = mealPlanRepository.getUserMealPlan(user.id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _userMealPlan.value = NetworkResult.Success(it)
                        userMealPlanLocal = it
                    } ?: run {
                        _userMealPlan.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _userMealPlan.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                _userMealPlan.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun addUserMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            _userMealPlan.value = NetworkResult.Loading
            try {
                val userMealPlan = UserMealPlan(null, user, mealPlan)
                val response = mealPlanRepository.bookMealPlan(userMealPlan)
                if (response.isSuccessful) {
                    response.body()?.let {
                        fetchUserMealPlan()
                    } ?: run {
                        _userMealPlan.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _userMealPlan.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                _userMealPlan.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun fetchMealPlans() {
        viewModelScope.launch {
            _mealPlans.value = NetworkResult.Loading
            try {
                val response = mealPlanRepository.getAllMealPlans()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _mealPlans.value = NetworkResult.Success(it)
                    } ?: run {
                        _mealPlans.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _mealPlans.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                _mealPlans.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun cancelMealPlan() {
        viewModelScope.launch {
            try {
                val response = mealPlanRepository.cancelMealPlan(userMealPlanLocal.id!!)
                if (response.isSuccessful) {
                    fetchUserMealPlan()
                    Log.d("MealPlanViewModel", "cancelMealPlan: ${response.body()}")
                } else {
                    _userMealPlan.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _userMealPlan.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }


    fun createStripePaymentIntent(amount: String) {
        val stripeConfigRequest = StripeConfigRequest(amount, user.id)
        viewModelScope.launch {
            try {
                val response = mealPlanRepository.getStripeClientSecret(stripeConfigRequest)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _stripeConfigResponse.value = NetworkResult.Success(it)
                    } ?: run {
                        _stripeConfigResponse.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _stripeConfigResponse.value = NetworkResult.Error("An error occurred")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _stripeConfigResponse.value = NetworkResult.Error("An error occurred")
            }
        }
    }
}