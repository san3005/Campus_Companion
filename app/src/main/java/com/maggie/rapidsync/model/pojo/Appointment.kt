package com.maggie.rapidsync.model.pojo

data class Appointment(
    val appointmentSchedule: AppointmentSchedule? = null,
    val teacher: User? = null,
    val student: User? = null
)

data class GroupedAppointment(
    val date: String? = null,
    val appointments: List<Appointment>? = null
)