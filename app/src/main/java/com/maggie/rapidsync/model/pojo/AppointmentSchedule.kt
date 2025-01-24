package com.maggie.rapidsync.model.pojo

data class AppointmentSchedule(
    var booked: Boolean,
    val date: String,
    val endTime: String,
    val id: String,
    val message: String,
    val startTime: String,
    var status: String,
    var studentId: String,
    val teacherId: String
)

