package com.maggie.rapidsync

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.R
import com.maggie.rapidsync.adapters.ParkingSlotAdapter
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.hide
import com.maggie.rapidsync.commons.show
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityParkingSlotsBinding
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.viewmodel.ParkingSlotViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ParkingSlotsActivity : AppCompatActivity() {

    private val binding: ActivityParkingSlotsBinding by lazy {
        ActivityParkingSlotsBinding.inflate(layoutInflater)
    }

    private val viewModel: ParkingSlotViewModel by viewModels()

    @Inject
    lateinit var localDataStore: LocalDataStore

    private lateinit var user: User

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        user = localDataStore.getObject(Constants.USER, object : TypeToken<User>() {})!!

        viewModel.getParkingSlots()

        lifecycleScope.launch {

            viewModel.parkingSlotUpdated.collect {
                when (it) {
                    is NetworkResult.Loading -> {
//                        showToast("Loading")
                    }

                    is NetworkResult.Success -> {
                        viewModel.getParkingSlots()
                        if (it.body?.available == false) {
                            showToast("Parking Slot Booked")
                        } else {
                            showToast("Parking Slot released")
                        }
                    }

                    is NetworkResult.Error -> {
                        showToast(it.errorMessage)
                    }

                    else -> {
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.parkingSlots.collect {
                when (it) {
                    is NetworkResult.Loading -> {
//                        showToast("Loading")
                    }

                    is NetworkResult.Success -> {
                        var booked = false

                        it.body?.firstOrNull { slot -> slot.user != null && user.id == slot.user.id }
                            ?.let { mySlot ->
                                booked = true
                                binding.textViewBookedSlot.show()
                                binding.cardViewParkingSlot.show()
                                binding.textViewLocation.text = "Location: ${mySlot.location}"
                                binding.textViewSpeciality.text =
                                    "Speciality: ${mySlot.speciality}"
                                binding.textViewId.text =
                                    mySlot.id.subSequence(mySlot.id.length - 2, mySlot.id.length)
                                binding.buttonReleaseSlot.setOnClickListener {
                                    viewModel.releaseParkingSlot(mySlot)
                                    binding.textViewBookedSlot.hide()
                                    binding.cardViewParkingSlot.hide()
                                }
                            }
                        binding.recyclerViewSlots.layoutManager =
                            androidx.recyclerview.widget.LinearLayoutManager(this@ParkingSlotsActivity)
                        binding.recyclerViewSlots.adapter = it.body?.let { slots ->
                            ParkingSlotAdapter(
                                slots.filter { slot -> slot.available },
                                booked
                            ) { parkingSlot ->
                                if (booked) {
                                    showToast("You have already booked a slot")
                                } else {
                                    viewModel.bookParkingSlot(parkingSlot)
                                }
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        showToast(it.errorMessage)
                    }

                    else -> {
                    }
                }
            }
        }
    }

    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, ParkingSlotsActivity::class.java)
        }

    }
}