package com.maggie.rapidsync.repo

import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.toErrorMessage
import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.Course
import com.maggie.rapidsync.model.pojo.CourseEnrollment
import com.maggie.rapidsync.model.pojo.Department
import com.maggie.rapidsync.model.pojo.Semester
import com.maggie.rapidsync.model.pojo.Subject
import com.maggie.rapidsync.model.pojo.University
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CourseRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getSubjects(): Flow<NetworkResult<List<Subject>>> = flow {
        try {
            val response = apiService.getSubjects()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun getUniversity(): Flow<NetworkResult<University>> = flow {
        try {
            val response = apiService.getUniversity()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }


    suspend fun getSemesters(): Flow<NetworkResult<List<Semester>>> = flow {
        try {
            val response = apiService.getSemesters()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun getDepartments(): Flow<NetworkResult<List<Department>>> = flow {
        try {
            val response = apiService.getDepartments()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun getCourses(): Flow<NetworkResult<List<Course>>> = flow {
        try {
            val response = apiService.getCourses()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun getCoursesByDepartment(departmentId: String): Flow<NetworkResult<List<Course>>> =
        flow {
            try {
                val response = apiService.getCoursesByDepartment(departmentId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(NetworkResult.Success(it))
                    }
                } else {
                    emit(
                        NetworkResult.Error(
                            response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                        )
                    )
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.message ?: "An error occurred"))
            }
        }

    suspend fun getCoursesBySemester(semesterId: String): Flow<NetworkResult<List<Course>>> = flow {
        try {
            val response = apiService.getCoursesBySemester(semesterId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }


    suspend fun getCourseEnrollmentsByStudent(studentId: String): Flow<NetworkResult<List<CourseEnrollment>>> =
        flow {
            try {
                val response = apiService.getCourseEnrollments(studentId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(NetworkResult.Success(it))
                    }
                } else {
                    emit(
                        NetworkResult.Error(
                            response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                        )
                    )
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.message ?: "An error occurred"))
            }
        }


    suspend fun enrollCourse(courseEnrollment: CourseEnrollment): Flow<NetworkResult<CourseEnrollment>> =
        flow {
            try {
                val response = apiService.enrollCourse(courseEnrollment)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(NetworkResult.Success(it))
                    }
                } else {
                    emit(
                        NetworkResult.Error(
                            response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                        )
                    )
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.message ?: "An error occurred"))
            }
        }

    suspend fun cancelCourseEnrollment(id: String): Flow<NetworkResult<CourseEnrollment>> = flow {
        try {
            val response = apiService.cancelCourseEnrollment(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Error("Course enrollment cancelled successfully"))
                }
            } else {
                emit(
                    NetworkResult.Error(
                        response.errorBody()?.toErrorMessage()?.message ?: "An error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Course enrollment cancelled successfully"))
        }
    }

}