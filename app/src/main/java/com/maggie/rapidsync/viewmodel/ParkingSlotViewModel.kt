package com.maggie.rapidsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.ParkingSlot
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.repo.ParkingSlotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkingSlotViewModel @Inject constructor(
    private val parkingSlotRepository: ParkingSlotRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {



    private lateinit var user: User

    init {
        viewModelScope.launch {
            user = localDataStore.getObject(Constants.USER, object : TypeToken<User>() {})!!
        }

    }
    private val _parkingSlots =
        MutableStateFlow<NetworkResult<List<ParkingSlot>>>(NetworkResult.Initial)
    val parkingSlots: StateFlow<NetworkResult<List<ParkingSlot>>>
        get() = _parkingSlots.asStateFlow()


    private val _parkingSlotUpdated =
        MutableStateFlow<NetworkResult<ParkingSlot>>(NetworkResult.Initial)

    val parkingSlotUpdated: StateFlow<NetworkResult<ParkingSlot>>
        get() = _parkingSlotUpdated.asStateFlow()


    fun getParkingSlots() {
        viewModelScope.launch {
            val res = parkingSlotRepository.getAll()
            if (res.isSuccessful) {
                res.body()?.let {
                    _parkingSlots.value = NetworkResult.Success(it)
                } ?: run {
                    _parkingSlots.value = NetworkResult.Error("An error occurred")
                }
            } else {
                _parkingSlots.value =
                    NetworkResult.Error(
                        res.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
            }
        }
    }

    fun bookParkingSlot(parkingSlot: ParkingSlot) {
        viewModelScope.launch {
            val res = parkingSlotRepository.updateParkingSlot(
                user.id,
                parkingSlot.copy(available = false, user = user)
            )
            if (res.isSuccessful) {
                res.body()?.let {
                    _parkingSlotUpdated.value = NetworkResult.Success(it)
                } ?: run {
                    _parkingSlotUpdated.value = NetworkResult.Error("An error occurred")
                }
            } else {
                _parkingSlotUpdated.value =
                    NetworkResult.Error(
                        res.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
            }
        }
    }

    fun releaseParkingSlot(parkingSlot: ParkingSlot) {
        viewModelScope.launch {
            val res = parkingSlotRepository.updateParkingSlot(
                user.id,
                parkingSlot.copy(available = true, user = null)
            )
            if (res.isSuccessful) {
                res.body()?.let {
                    _parkingSlotUpdated.value = NetworkResult.Success(it)
                } ?: run {
                    _parkingSlotUpdated.value = NetworkResult.Error("An error occurred")
                }
            } else {
                _parkingSlotUpdated.value =
                    NetworkResult.Error(
                        res.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
            }
        }
    }

}