package com.maggie.rapidsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.AppointmentSchedule
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {

    private val _user = MutableStateFlow<NetworkResult<User>>(NetworkResult.Initial)
    val user: MutableStateFlow<NetworkResult<User>>
        get() = _user

    private val _appointments =
        MutableStateFlow<NetworkResult<List<AppointmentSchedule>>>(NetworkResult.Initial)
    val appointments: MutableStateFlow<NetworkResult<List<AppointmentSchedule>>>
        get() = _appointments


    private lateinit var localUser: User

    init {
        viewModelScope.launch {
            localUser = localDataStore.getObject(Constants.USER, object : TypeToken<User>() {})!!
        }

    }

    fun getUser() {
        viewModelScope.launch {
            _user.value = NetworkResult.Loading
            userRepository.getUser(localUser.id).let {
                if (it.isSuccessful) {
                    it.body()?.let { user ->
                        _user.value = NetworkResult.Success(user)
                        localDataStore.saveObject("user", user)
                    } ?: run {
                        _user.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _user.value = NetworkResult.Error("An error occurred")
                }
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _user.value = NetworkResult.Loading
            userRepository.updateUser(user.id, user).let {
                if (it.isSuccessful) {
                    it.body()?.let { user ->
                        _user.value = NetworkResult.Success(user)
                        localDataStore.saveObject("user", user)
                    } ?: run {
                        _user.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _user.value = NetworkResult.Error("An error occurred")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            localDataStore.clearPreferences()
        }
    }

}