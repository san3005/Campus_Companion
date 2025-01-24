package com.maggie.rapidsync

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.maggie.rapidsync.adapters.CompletedCourseAdapter
import com.maggie.rapidsync.adapters.CourseAdapter
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.hide
import com.maggie.rapidsync.commons.show
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityProfileBinding
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.viewmodel.CourseViewModel
import com.maggie.rapidsync.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private val coursesViewModel: CourseViewModel by viewModels()


    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: UserViewModel by viewModels()


    private lateinit var localUser: User

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }

        lifecycleScope.launch {
            viewModel.getUser()
            viewModel.user.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val user = result.body!!
                        localUser = user

                        binding.imageViewProfile.load(
                            user.profileImageUrl.ifEmpty {
                                R.drawable.tamucc
                            }
                        ) {
                            crossfade(true)
                            transformations(CircleCropTransformation())
                        }
                        binding.editProfileButton.show()
                        binding.textViewUsername.text = user.username
                        binding.textViewUsername1.text = user.username
                        binding.textViewEmail.text = user.email
                        user.address?.let {
                            binding.address.text = it
                        }
                        user.phone?.let {
                            binding.number.text = it
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

            lifecycleScope.launch {
                coursesViewModel.cancelCourseEnrollment.collect { result ->
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

        binding.editProfileButton.setOnClickListener {
            showUpdateDialog()
        }
    }


    private fun observeCourses() {
        lifecycleScope.launch {
            coursesViewModel.getEnrollments.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.body?.let { enrollments ->
                            binding.coursesRecyclerView.layoutManager =
                                LinearLayoutManager(this@ProfileActivity)
                            binding.coursesRecyclerView.adapter =
                                CourseAdapter(enrollments.filter { it.status == "Enrolled" }
                                    .map { it.course }, true, listOf()) { selectedCourse ->
                                    lifecycleScope.launch {
                                        val courseEnrollment =
                                            enrollments.firstOrNull { courseEnrollment ->
                                                courseEnrollment.course.id == selectedCourse.id
                                            }
                                        if (courseEnrollment != null) {
                                            coursesViewModel.cancelCourseEnrollment(courseEnrollment)
                                        } else {
                                            showToast("Course enrollment not found")
                                        }

                                    }
                                }

                            binding.completedCoursesRecyclerView.layoutManager =
                                LinearLayoutManager(this@ProfileActivity)
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
        binding.editProfileButton.setOnClickListener {
            showUpdateDialog()
        }

    }

    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, ProfileActivity::class.java)
        }
    }


    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.update_profile, null)
        val editTextUsername = dialogView.findViewById<EditText>(R.id.textViewUsername1)
        val editTextPhoneNumber = dialogView.findViewById<EditText>(R.id.number)
        val editTextAddress = dialogView.findViewById<EditText>(R.id.address)
        localUser.let {
            editTextUsername.setText(it.username)
            editTextPhoneNumber.setText(it?.phone)
            editTextAddress.setText(it?.address)
        }

        builder.setView(dialogView)
            .setTitle("Update profile")
            .setPositiveButton("Update") { _, _ ->
                val newUsername = editTextUsername.text.toString().trim()
                val newPhoneNumber = editTextPhoneNumber.text.toString().trim()
                val newAddress = editTextAddress.text.toString().trim()

                if (newUsername.isEmpty() || newPhoneNumber.isEmpty() || newAddress.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    if ((newPhoneNumber.length == 10)) {
                        viewModel.updateUser(
                            user = localUser.copy(
                                username = newUsername,
                                phone = newPhoneNumber,
                                address = newAddress
                            )
                        )
                    } else {
                        Toast.makeText(this, "Invalid phone number format", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val alertDialog = builder.create()
        alertDialog.show()
    }


}