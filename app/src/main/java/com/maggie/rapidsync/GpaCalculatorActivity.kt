package com.maggie.rapidsync

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.maggie.rapidsync.adapters.GpaCourseAdapter
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityGpaCalculatorBinding
import com.maggie.rapidsync.model.pojo.GpaCourse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class GpaCalculatorActivity : AppCompatActivity() {


    private val binding: ActivityGpaCalculatorBinding by lazy {
        ActivityGpaCalculatorBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: GpaCourseAdapter
    private val courseList = mutableListOf<GpaCourse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonAddCourse.setOnClickListener {
            addCourse()
        }

        binding.recyclerViewCourses.layoutManager = LinearLayoutManager(this)
        adapter = GpaCourseAdapter(courseList) { course ->
            courseList.remove(course)
            adapter.notifyDataSetChanged()
            updateTotalGPA()
        }
        binding.recyclerViewCourses.adapter = adapter

        val grades = arrayOf("A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F")
        val credits = arrayOf("1", "2", "3", "4", "5")
        binding.spinnerGrade.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, grades)
        binding.spinnerCredits.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, credits)


    }


    private fun addCourse() {
        val courseName = binding.editTextCourseName.text.toString()
        val grade = binding.spinnerGrade.selectedItem.toString()
        val credits = binding.spinnerCredits.selectedItem.toString().toInt()

        if (courseName.isNotEmpty()) {
            val course = GpaCourse(courseName, credits, grade)
            courseList.add(course)
            adapter.notifyDataSetChanged()
            updateTotalGPA()
            clearFields()
        } else {
            showToast("Please enter a course name")
        }
    }

    private fun updateTotalGPA() {
        val totalGPA = calculateTotalGPA()
        binding.textViewTotalGPA.text = totalGPA.toString()
    }

    private fun clearFields() {
        binding.editTextCourseName.text.clear()
        binding.spinnerGrade.setSelection(0)
        binding.spinnerCredits.setSelection(0)
    }

    private fun calculateTotalGPA(): Double {
        var totalGradePoints = 0.0
        var totalCredits = 0

        for (course in courseList) {
            val gradePoints = getGradePoints(course.courseGrade) * course.courseCredit
            totalGradePoints += gradePoints
            totalCredits += course.courseCredit
        }

        return if (totalCredits > 0) {
            (totalGradePoints / totalCredits * 100).toInt() / 100.0
        } else {
            0.0
        }
    }

    private fun getGradePoints(grade: String): Double {
        return when (grade) {
            "A" -> 4.0
            "A-" -> 3.7
            "B+" -> 3.3
            "B" -> 3.0
            "B-" -> 2.7
            "C+" -> 2.3
            "C" -> 2.0
            "C-" -> 1.7
            "D+" -> 1.3
            "D" -> 1.0
            "F" -> 0.0
            else -> 0.0
        }
    }

    companion object {
        fun newIntent(dashBoardActivity: AppCompatActivity): Intent {
            return Intent(dashBoardActivity, GpaCalculatorActivity::class.java)
        }
    }

}