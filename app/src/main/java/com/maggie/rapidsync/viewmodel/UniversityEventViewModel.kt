package com.maggie.rapidsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.pojo.UniversityEvent
import com.maggie.rapidsync.repo.UniversityEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UniversityEventViewModel @Inject constructor(
    private val universityEventRepository: UniversityEventRepository
) : ViewModel() {

    private val _universityEvent =
        MutableStateFlow<NetworkResult<List<UniversityEvent>>>(NetworkResult.Loading)
    val universityEvent: StateFlow<NetworkResult<List<UniversityEvent>>> =
        _universityEvent.asStateFlow()

    fun getUniversityEvent() {
        viewModelScope.launch {
            _universityEvent.value = NetworkResult.Loading
            try {
                val response = universityEventRepository.getAll()
                if (response.isSuccessful) {
                    _universityEvent.value = response.body()?.let {
                        NetworkResult.Success(it)
                    } ?: NetworkResult.Error(response.message())
                } else {
                    _universityEvent.value = NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                }

            } catch (e: Exception) {
                _universityEvent.value = NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

}