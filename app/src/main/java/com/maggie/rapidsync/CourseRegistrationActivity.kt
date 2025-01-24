package com.maggie.rapidsync

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maggie.rapidsync.adapters.CourseAdapter
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityCourseRegistrationBinding
import com.maggie.rapidsync.model.pojo.CourseEnrollment
import com.maggie.rapidsync.model.pojo.Semester
import com.maggie.rapidsync.viewmodel.CourseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourseRegistrationActivity : AppCompatActivity() {

    val binding by lazy { ActivityCourseRegistrationBinding.inflate(layoutInflater) }

    private val viewModel: CourseViewModel by viewModels()

    private lateinit var selectedSemester: Semester

    val myCourses = mutableListOf<CourseEnrollment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeStudentCourses()
        getSemesters()


        lifecycleScope.launch {
            viewModel.enrollCourse.collect {
                when (it) {
                    is NetworkResult.Success -> {
                        showToast("Course enrolled successfully")
                    }

                    is NetworkResult.Error -> {
                        showToast(it.errorMessage)
                    }

                    else -> {}
                }
            }
        }


    }

    private fun observeStudentCourses() {
        lifecycleScope.launch {
            viewModel.getEnrollments.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.body?.let { enrollments ->
                            myCourses.addAll(enrollments)
                            observeCourses()
                        }
                    }

                    is NetworkResult.Error -> {
                        showToast(result.errorMessage)
                    }

                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }


    private fun observeCourses() {
        lifecycleScope.launch {
            viewModel.coursesBySemester.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.body?.let { courses ->
                            binding.coursesRecyclerView.layoutManager =
                                LinearLayoutManager(this@CourseRegistrationActivity)
                            binding.coursesRecyclerView.adapter = CourseAdapter(
                                courses,
                                false,
                                myCourses = myCourses.map { it.course }) {

                                if (selectedSemester.status != "active") {
                                    showToast("Semester is not active, you can't enroll in any course.")
                                    return@CourseAdapter
                                }

                                lifecycleScope.launch {
                                    viewModel.enrollCourse(it)
                                }
                            }
                        }

                    }

                    is NetworkResult.Error -> {
                        showToast(result.errorMessage)
                    }

                    else -> {}
                }
            }
        }
    }


    private fun getSemesters() {
        lifecycleScope.launch {
            viewModel.getSemesters()

            viewModel.semesters.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val semesters = result.body!!
                        val semesterAdapter = ArrayAdapter(
                            this@CourseRegistrationActivity,
                            R.layout.simple_spinner_dropdown_item,
                            semesters.map { it.name })
                        binding.spinnerSemester.adapter = semesterAdapter
                        binding.spinnerSemester.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val semester = semesters[position]
                                    selectedSemester = semester
                                    viewModel.getCoursesBySemester(semester.id)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }
                            }
                    }

                    is NetworkResult.Error -> {
                        showToast(result.errorMessage)
                    }

                    else -> {}
                }

            }
        }
    }

    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, CourseRegistrationActivity::class.java)
        }
    }
}