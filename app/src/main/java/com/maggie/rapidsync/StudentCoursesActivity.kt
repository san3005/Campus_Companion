package com.maggie.rapidsync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maggie.rapidsync.adapters.CompletedCourseAdapter
import com.maggie.rapidsync.adapters.CourseAdapter
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.hide
import com.maggie.rapidsync.commons.show
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityStudentCoursesBinding
import com.maggie.rapidsync.viewmodel.CourseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentCoursesActivity : AppCompatActivity() {

    private val binding by lazy { ActivityStudentCoursesBinding.inflate(layoutInflater) }

    private val viewModel: CourseViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeCourses()
        binding.selection.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.current -> {
                    binding.coursesRecyclerView.show()
                    binding.completedCoursesRecyclerView.hide()
                }

                R.id.completed -> {
                    binding.coursesRecyclerView.hide()
                    binding.completedCoursesRecyclerView.show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.cancelCourseEnrollment.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        showToast("Course enrollment cancelled")
                    }

                    is NetworkResult.Error -> {
                        showToast(result.errorMessage)
                    }

                    else -> {}
                }

            }

        }
    }

    private fun observeCourses() {
        lifecycleScope.launch {
            viewModel.getEnrollments.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.body?.let { enrollments ->
                            binding.coursesRecyclerView.layoutManager =
                                LinearLayoutManager(this@StudentCoursesActivity)
                            binding.coursesRecyclerView.adapter =
                                CourseAdapter(enrollments.filter { it.status == "Enrolled" }
                                    .map { it.course }, true, listOf()) { selectedCourse ->
                                    lifecycleScope.launch {
                                        val courseEnrollment =
                                            enrollments.firstOrNull { courseEnrollment ->
                                                courseEnrollment.course.id == selectedCourse.id
                                            }
                                        if (courseEnrollment != null) {
                                            viewModel.cancelCourseEnrollment(courseEnrollment)
                                        } else {
                                            showToast("Course enrollment not found")
                                        }

                                    }
                                }

                            binding.completedCoursesRecyclerView.layoutManager =
                                LinearLayoutManager(this@StudentCoursesActivity)
                            binding.completedCoursesRecyclerView.adapter =
                                CompletedCourseAdapter(enrollments.filter { it.status != "Enrolled" })
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
            return Intent(dashBoardActivity, StudentCoursesActivity::class.java)
        }
    }
}