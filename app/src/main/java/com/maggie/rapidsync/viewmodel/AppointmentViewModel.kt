package com.maggie.rapidsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.Appointment
import com.maggie.rapidsync.model.pojo.AppointmentSchedule
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.repo.AppointmentsRepository
import com.maggie.rapidsync.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val localDataStore: LocalDataStore,
    private val appointmentsRepository: AppointmentsRepository
) : ViewModel() {


    private val _appointments =
        MutableStateFlow<NetworkResult<List<Appointment>>>(NetworkResult.Initial)
    val appointments = _appointments.asStateFlow()

    private val _teacherAppointments =
        MutableStateFlow<NetworkResult<List<Appointment>>>(NetworkResult.Initial)
    val teacherAppointments = _teacherAppointments.asStateFlow()


    private val _teachers = MutableStateFlow<NetworkResult<List<User>>>(NetworkResult.Initial)
    val teachers = _teachers.asStateFlow()

    private val _message = MutableStateFlow<NetworkResult<String>>(NetworkResult.Initial)
    val message = _message.asStateFlow()


    private lateinit var localUser: User

    init {
        viewModelScope.launch {
            localUser = localDataStore.getObject(Constants.USER, object : TypeToken<User>() {})!!
        }

    }

    fun getAppointments() {
        viewModelScope.launch {
            appointmentsRepository.getAppointmentSchedules(localUser.id).let {
                if (it.isSuccessful) {
                    it.body()?.let { appointments ->
                        _appointments.value = NetworkResult.Success(appointments)
                    } ?: run {
                        _appointments.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _appointments.value = NetworkResult.Error("An error occurred")
                }
            }
        }
    }


    fun getTeacherAppointments(teacherId: String) {
        viewModelScope.launch {
            appointmentsRepository.getTeacherAppointmentSchedules(teacherId).let {
                if (it.isSuccessful) {
                    it.body()?.let { appointments ->
                        _teacherAppointments.value = NetworkResult.Success(appointments)
                    } ?: run {
                        _teacherAppointments.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _teacherAppointments.value = NetworkResult.Error("An error occurred")
                }
            }
        }
    }


    fun getTeachers() {
        viewModelScope.launch {
            appointmentsRepository.getTeachers().let {
                if (it.isSuccessful) {
                    it.body()?.let { teachers ->
                        _teachers.value = NetworkResult.Success(teachers)
                    } ?: run {
                        _teachers.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _teachers.value = NetworkResult.Error("An error occurred")
                }
            }
        }
    }

    fun updateAppointment(appointmentSchedule: AppointmentSchedule) {
        viewModelScope.launch {
            appointmentsRepository.updateAppointment(appointmentSchedule).let {
                if (it.isSuccessful) {
                    it.body()?.let { appointment ->
                        getAppointments()
                        _message.value =
                            NetworkResult.Success(
                                if (appointment.booked)
                                    "Appointment requested successfully"
                                else
                                    "Appointment cancelled successfully")
                    } ?: run {
                        _message.value = NetworkResult.Error("An error occurred")
                    }
                } else {
                    _message.value = NetworkResult.Error("An error occurred")
                }
            }
        }
    }
}