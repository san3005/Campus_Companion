package com.maggie.rapidsync.repo

import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.Appointment
import com.maggie.rapidsync.model.pojo.AppointmentSchedule
import com.maggie.rapidsync.model.pojo.User
import retrofit2.Response
import javax.inject.Inject

class AppointmentsRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getTeachers(): Response<List<User>> {
        return apiService.getTeachers()
    }

    suspend fun getAppointmentSchedules(id: String): Response<List<Appointment>> {
        return apiService.getAppointmentSchedules(id)
    }

    suspend fun getTeacherAppointmentSchedules(id: String): Response<List<Appointment>> {
        return apiService.getTeacherAppointmentSchedules(id)
    }


    suspend fun updateAppointment(appointmentSchedule: AppointmentSchedule): Response<AppointmentSchedule> {
        return apiService.updateAppointment(appointmentSchedule.id, appointmentSchedule)
    }


}