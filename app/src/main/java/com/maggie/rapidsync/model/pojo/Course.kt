package com.maggie.rapidsync.model.pojo

data class Course(
    val classRoom: String,
    val days: String,
    val departmentId: String,
    val endTime: String,
    val id: String,
    val instructor: User,
    val semesterId: String,
    val startTime: String,
    val subject: Subject,
    val maxStudents: Int,
    val currentStudents: Int
)


