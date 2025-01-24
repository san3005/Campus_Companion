package com.maggie.rapidsync.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.LoginRequest
import com.maggie.rapidsync.model.pojo.LoginResponse
import com.maggie.rapidsync.model.pojo.SaveFcmRequest
import com.maggie.rapidsync.repo.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {


    private val _loginResponse =
        MutableStateFlow<NetworkResult<LoginResponse>>(NetworkResult.Initial)

    val loginResponse: StateFlow<NetworkResult<LoginResponse>>
        get() = _loginResponse.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val res = loginRepository.login(LoginRequest(email, password))
                if (res.isSuccessful) {
                    res.body()?.let {
                        if (it.user.roles.any { role -> role.name.contains("ROLE_STUDENT") }) {

                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val token = task.result.toString()
                                    Log.e("FCM Token", token)
                                    viewModelScope.launch {
                                        saveToken(it.user.id, token, it)
                                    }
                                }
                            }
                            // Subscribe to all topics
                            FirebaseMessaging.getInstance().subscribeToTopic("all");

                            localDataStore.saveString(Constants.TOKEN, it.token)
                            localDataStore.saveObject(Constants.USER, it.user)
                            localDataStore.saveBoolean(Constants.IS_LOGGED_IN, true)

                        } else {
                            _loginResponse.value =
                                NetworkResult.Error("Not a student account. Please use web panel.")
                        }

                    } ?: run {
                        _loginResponse.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _loginResponse.value =
                        NetworkResult.Error(
                            res.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                        )
                }

            } catch (e: Exception) {
                _loginResponse.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun saveToken(userId: String, token: String, loginResponse: LoginResponse) {
        viewModelScope.launch {
            try {
                val res = loginRepository.saveToken(SaveFcmRequest(fcmToken = token, userId = userId))
                if (res.isSuccessful) {
                    res.body()?.let {
                        localDataStore.saveObject(Constants.USER, it)
                        _loginResponse.value = NetworkResult.Success(loginResponse)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}