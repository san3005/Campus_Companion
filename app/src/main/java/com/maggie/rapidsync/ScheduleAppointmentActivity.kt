package com.maggie.rapidsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.adapters.AppointmentAdapter
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityStudentScheduleAppointmentBinding
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.GroupedAppointment
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.viewmodel.AppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleAppointmentActivity : AppCompatActivity() {

    private val viewModel: AppointmentViewModel by viewModels()

    val binding: ActivityStudentScheduleAppointmentBinding by lazy {
        ActivityStudentScheduleAppointmentBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var localDataSource: LocalDataStore


    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        user = localDataSource.getObject(Constants.USER, object : TypeToken<User>() {})!!

        lifecycleScope.launch {
            viewModel.teacherAppointments.collect { it ->
                when (it) {
                    is NetworkResult.Error -> {
                        // Handle error
                    }

                    NetworkResult.Initial -> {
                        // Handle initial
                    }

                    NetworkResult.Loading -> {
                        // Handle loading
                    }

                    is NetworkResult.Success -> {
                        it.body?.let { appointments ->
                            val map =
                                appointments.filter { it.appointmentSchedule?.booked == false }
                                    .groupBy { app -> app.appointmentSchedule?.date }
                            val groupedAppointments = map.map { entry ->
                                GroupedAppointment(entry.key, entry.value)
                            }

                            binding.appointmentRecyclerView.layoutManager =
                                androidx.recyclerview.widget.LinearLayoutManager(this@ScheduleAppointmentActivity)
                            binding.appointmentRecyclerView.adapter =
                                AppointmentAdapter(groupedAppointments, true) {
                                    it.appointmentSchedule?.copy(
                                        studentId = user.id,
                                        booked = true,
                                        status = "Requested",
                                        message = "Requested by ${user.username}"
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
            viewModel.message.collect {
                when (it) {
                    is NetworkResult.Error -> {
                        // Handle error
                    }

                    NetworkResult.Initial -> {
                        // Handle initial
                    }

                    NetworkResult.Loading -> {
                        // Handle loading
                    }

                    is NetworkResult.Success -> {
                        // Handle message
                        showToast(it.body ?: "Success")
                        finish()
                    }
                }
                // Handle message
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTeacherAppointments(intent.getStringExtra("teacherId") ?: "")
    }

    companion object {
        fun newIntent(studentScheduleAppointment: StudentScheduleAppointment): Intent {
            return Intent(studentScheduleAppointment, ScheduleAppointmentActivity::class.java)
        }
    }
}