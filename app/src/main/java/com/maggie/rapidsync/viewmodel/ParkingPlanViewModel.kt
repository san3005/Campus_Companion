package com.maggie.rapidsync.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.Constants.USER
import com.maggie.rapidsync.model.pojo.ParkingPlan
import com.maggie.rapidsync.model.pojo.StripeConfigRequest
import com.maggie.rapidsync.model.pojo.StripeConfigResponse
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.model.pojo.UserParkingPlan
import com.maggie.rapidsync.repo.ParkingPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkingPlanViewModel @Inject constructor(
    private val parkingPlanRepository: ParkingPlanRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {

    private lateinit var user: User

    init {
        viewModelScope.launch {
            user = localDataStore.getObject(Constants.USER, object : TypeToken<User>() {})!!
        }

    }

    private lateinit var userParkingPlanLocal: UserParkingPlan

    private val _parkingPlans =
        MutableStateFlow<NetworkResult<List<ParkingPlan>>>(NetworkResult.Initial)
    val parkingPlans: StateFlow<NetworkResult<List<ParkingPlan>>> = _parkingPlans.asStateFlow()


    private val _userParkingPlan =
        MutableStateFlow<NetworkResult<UserParkingPlan>>(NetworkResult.Initial)
    val userParkingPlan: StateFlow<NetworkResult<UserParkingPlan>> = _userParkingPlan.asStateFlow()

    fun fetchUserParkingPlan() {
        viewModelScope.launch {
            _userParkingPlan.value = NetworkResult.Loading
            try {
                val response = parkingPlanRepository.getUserParkingPlans(user.id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _userParkingPlan.value = NetworkResult.Success(it)
                        userParkingPlanLocal = it
                    } ?: run {
                        _userParkingPlan.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _userParkingPlan.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                _userParkingPlan.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun addUserParkingPlan(parkingPlan: ParkingPlan) {
        viewModelScope.launch {
            _userParkingPlan.value = NetworkResult.Loading
            try {
                val userParkingPlan = UserParkingPlan(null, user, parkingPlan)
                val response = parkingPlanRepository.bookParkingPlan(userParkingPlan)
                if (response.isSuccessful) {
                    response.body()?.let {
                        userParkingPlanLocal = it
                        _userParkingPlan.value = NetworkResult.Success(it)
                    } ?: run {
                        _userParkingPlan.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _userParkingPlan.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                _userParkingPlan.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun fetchParkingPlans() {
        viewModelScope.launch {
            _parkingPlans.value = NetworkResult.Loading
            try {
                val response = parkingPlanRepository.getAll()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _parkingPlans.value = NetworkResult.Success(it)
                    } ?: run {
                        _parkingPlans.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _parkingPlans.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                _parkingPlans.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun cancelParkingPlan() {
        viewModelScope.launch {
            try {
                val response = parkingPlanRepository.cancelParkingPlan(userParkingPlanLocal)
                if (response.isSuccessful) {
                    fetchUserParkingPlan()

                    Log.d("ParkingPlanViewModel", "cancelParkingPlan: ${response.body()}")
                } else {
                    _userParkingPlan.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _userParkingPlan.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }
    private val _stripeConfigResponse =
        MutableStateFlow<NetworkResult<StripeConfigResponse>>(NetworkResult.Initial)
    val stripeConfigResponse: StateFlow<NetworkResult<StripeConfigResponse>> =
        _stripeConfigResponse.asStateFlow()


    fun createStripePaymentIntent(amount: String) {
        val stripeConfigRequest = StripeConfigRequest(amount, user.id)
        viewModelScope.launch {
            try {
                val response = parkingPlanRepository.getStripeClientSecret(stripeConfigRequest)
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