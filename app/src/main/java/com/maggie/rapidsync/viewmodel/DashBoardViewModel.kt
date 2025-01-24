package com.maggie.rapidsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.LoginRequest
import com.maggie.rapidsync.model.pojo.LoginResponse
import com.maggie.rapidsync.repo.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {


    private val _loginResponse =
        MutableStateFlow<NetworkResult<LoginResponse>>(NetworkResult.Initial)

    val loginResponse: StateFlow<NetworkResult<LoginResponse>>
        get() = _loginResponse.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val res = loginRepository.login(LoginRequest(email, password))
            if (res.isSuccessful) {
                res.body()?.let {
                    _loginResponse.value = NetworkResult.Success(it)
                    localDataStore.saveString(Constants.TOKEN, it.token)
                    localDataStore.saveObject(Constants.USER, it.user)
                    localDataStore.saveBoolean(Constants.IS_LOGGED_IN, true)
                } ?: run {
                    _loginResponse.value = NetworkResult.Error("An error occurred")
                }
            } else {
                _loginResponse.value =
                    NetworkResult.Error(
                        res.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
            }
        }
    }

}