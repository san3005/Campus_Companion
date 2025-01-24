package com.maggie.rapidsync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.maggie.rapidsync.adapters.AppointmentAdapter
import com.maggie.rapidsync.adapters.TeachersAdapter
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityStudentScheduleAppointmentBinding
import com.maggie.rapidsync.model.pojo.GroupedAppointment
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.viewmodel.AppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StudentScheduleAppointment : AppCompatActivity() {

    private val viewModel: AppointmentViewModel by viewModels()

    val binding: ActivityStudentScheduleAppointmentBinding by lazy {
        ActivityStudentScheduleAppointmentBinding.inflate(layoutInflater)
    }

    private val teachers = mutableListOf<User>()


    override fun onResume() {
        super.onResume()
        viewModel.getAppointments()
        viewModel.getTeachers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.appointments.collect { it ->
                when (it) {
                    is NetworkResult.Error -> {

                    }

                    NetworkResult.Initial -> {

                    }

                    NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                        it.body?.let { appoitments ->

                            val map = appoitments.groupBy { app -> app.appointmentSchedule?.date }
                            val groupedAppointments = map.map { entry ->
                                GroupedAppointment(entry.key, entry.value)
                            }
                            binding.appointmentRecyclerView.layoutManager =
                                androidx.recyclerview.widget.LinearLayoutManager(this@StudentScheduleAppointment)
                            binding.appointmentRecyclerView.adapter =
                                AppointmentAdapter(groupedAppointments, false) {
                                    it.appointmentSchedule?.copy(
                                        studentId = "",
                                        booked = false,
                                        status = "Available"
                                    )?.let { it1 ->
                                        viewModel.updateAppointment(
                                            it1
                                        )
                                    }
                                }

                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.teachers.collect {
                when (it) {
                    is NetworkResult.Error -> {

                    }

                    NetworkResult.Initial -> {

                    }

                    NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                        it.body?.let { users ->
                            teachers.clear()
                            teachers.addAll(users)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.message.collect {
                when (it) {
                    is NetworkResult.Error -> {

                    }

                    NetworkResult.Initial -> {

                    }

                    NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                        it.body?.let { message ->
                            // Handle success
                            showToast(message)
                        }

                    }
                }
            }
        }

        binding.addAppointmentBtn.setOnClickListener {
            showAddAppointmentDialog()
        }
    }

    @SuppressLint("InflateParams")
    private fun showAddAppointmentDialog() {

        val view: View = LayoutInflater.from(this).inflate(R.layout.teachers_dialog, null)
        val listView = view.findViewById<RecyclerView>(R.id.professors)
        val bottomSheetDialog = BottomSheetDialog(this)

        listView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        listView.setAdapter(TeachersAdapter(teachers) {
            val intent = ScheduleAppointmentActivity.newIntent(this)
            intent.putExtra("teacherId", it.id)
            bottomSheetDialog.dismiss()
            startActivity(intent)
        })
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, StudentScheduleAppointment::class.java)
        }
    }
}