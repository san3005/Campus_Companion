package com.maggie.rapidsync.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.Course
import com.maggie.rapidsync.model.pojo.CourseEnrollment
import com.maggie.rapidsync.model.pojo.Department
import com.maggie.rapidsync.model.pojo.Semester
import com.maggie.rapidsync.model.pojo.University
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.repo.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val localDataStore: LocalDataStore
) : ViewModel() {


    private lateinit var user: User

    init {
        viewModelScope.launch {
            user = localDataStore.getObject(Constants.USER, object : TypeToken<User>() {})!!
        }

    }
    private val _courses = MutableStateFlow<NetworkResult<List<Course>>>(NetworkResult.Initial)
    val courses = _courses.asStateFlow()

    private val _university = MutableStateFlow<NetworkResult<University>>(NetworkResult.Initial)
    val university = _university.asStateFlow()

    private val _semesters = MutableStateFlow<NetworkResult<List<Semester>>>(NetworkResult.Initial)
    val semesters = _semesters.asStateFlow()

    private val _departments =
        MutableStateFlow<NetworkResult<List<Department>>>(NetworkResult.Initial)
    val departments = _departments.asStateFlow()

    private val _getEnrollments =
        MutableStateFlow<NetworkResult<List<CourseEnrollment>>>(NetworkResult.Initial)
    val getEnrollments = _getEnrollments.asStateFlow()

    private val _coursesBySemester =
        MutableStateFlow<NetworkResult<List<Course>>>(NetworkResult.Initial)
    val coursesBySemester = _coursesBySemester.asStateFlow()

    private val _coursesByDepartment =
        MutableStateFlow<NetworkResult<List<Course>>>(NetworkResult.Initial)
    val coursesByDepartment = _coursesByDepartment.asStateFlow()

    private val _enrollCourse =
        MutableStateFlow<NetworkResult<CourseEnrollment>>(NetworkResult.Initial)
    val enrollCourse = _enrollCourse.asStateFlow()

    private val _cancelCourseEnrollment =
        MutableStateFlow<NetworkResult<CourseEnrollment>>(NetworkResult.Initial)
    val cancelCourseEnrollment = _cancelCourseEnrollment.asStateFlow()

    private val _toastMessage = MutableStateFlow<String>("")
    val toastMessage = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            getStudentEnrollments()
        }
    }

    fun cancelCourseEnrollment(courseEnrollment: CourseEnrollment) {
        viewModelScope.launch {
            repository.cancelCourseEnrollment(courseEnrollment.id!!).collect {
                _cancelCourseEnrollment.value = it
                getStudentEnrollments()
            }
        }
    }

    fun enrollCourse(course: Course) {
        when (val response = _getEnrollments.value) {
            is NetworkResult.Success -> {
                val courses = response.body
                if (courses?.any { it.course.id == course.id } == true) {
                    _enrollCourse.value =
                        NetworkResult.Error("You are already enrolled in this course")
                    return
                }
            }

            is NetworkResult.Error -> {
                Log.d("CourseViewModel", "enrollCourse: ${response.errorMessage}")
                _toastMessage.value = response.errorMessage
            }

            NetworkResult.Initial -> {
                Log.d("CourseViewModel", "enrollCourse: Initial...")
            }

            NetworkResult.Loading -> {
                Log.d("CourseViewModel", "enrollCourse: Loading...")
                _toastMessage.value = "Please wait..."
            }
        }

        viewModelScope.launch {
            repository.enrollCourse(
                CourseEnrollment(
                    student = user,
                    course = course,
                    completionDate = "",
                    completionStatus = "",
                    feedback = "",
                    grade = "",
                    id = null,
                    status = "Enrolled"
                )
            ).collect {
                _enrollCourse.value = it
                getStudentEnrollments()
                getCoursesBySemester(course.semesterId)

            }
        }
    }

    private fun getStudentEnrollments() {
        viewModelScope.launch {
            repository.getCourseEnrollmentsByStudent(user.id).collect {
                _getEnrollments.value = it
            }
        }
    }

    fun getDepartments() {
        viewModelScope.launch {
            repository.getDepartments().collect {
                _departments.value = it
            }
        }
    }

    fun getSemesters() {
        viewModelScope.launch {
            repository.getSemesters().collect {
                _semesters.value = it
            }
        }
    }

    fun getCourses() {
        viewModelScope.launch {
            repository.getCourses().collect {
                _courses.value = it
            }
        }
    }

    fun getCoursesBySemester(semesterId: String) {
        viewModelScope.launch {
            repository.getCoursesBySemester(semesterId).collect {
                _coursesBySemester.value = it
            }
        }
    }

    fun getCoursesByDepartment(departmentId: String) {
        viewModelScope.launch {
            repository.getCoursesByDepartment(departmentId).collect {
                _coursesByDepartment.value = it
            }
        }
    }


    fun getUniversity() {
        viewModelScope.launch {
            repository.getUniversity().collect {
                _university.value = it
            }
        }
    }


}