package com.maggie.rapidsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.model.pojo.MapLocation
import com.maggie.rapidsync.repo.MapLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapLocationViewModel @Inject constructor(
    private val mapLocationRepository: MapLocationRepository
) : ViewModel() {

    private val _mapLocations =
        MutableStateFlow<NetworkResult<List<MapLocation>>>(NetworkResult.Initial)
    val mapLocations: StateFlow<NetworkResult<List<MapLocation>>> = _mapLocations.asStateFlow()


    fun fetchMapLocations() {
        viewModelScope.launch {
            mapLocationRepository.getAllLocations().collect {
                _mapLocations.value = it
            }
        }
    }

}