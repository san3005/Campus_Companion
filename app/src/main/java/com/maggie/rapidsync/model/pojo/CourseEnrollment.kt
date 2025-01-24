package com.maggie.rapidsync.model.pojo

data class CourseEnrollment(
    val completionDate: String,
    val completionStatus: String,
    val course: Course,
    val feedback: String,
    val grade: String,
    val id: String?,
    val status: String,
    val student: User
)