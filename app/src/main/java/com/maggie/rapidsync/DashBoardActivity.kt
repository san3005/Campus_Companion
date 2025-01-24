package com.maggie.rapidsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.maggie.rapidsync.adapters.UniversityEventAdapter
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.show
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityDashBoardBinding
import com.maggie.rapidsync.viewmodel.UniversityEventViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashBoardActivity : AppCompatActivity(), OnMapReadyCallback {


    private val universityEventViewModel: UniversityEventViewModel by viewModels()

    private val binding: ActivityDashBoardBinding by lazy {
        ActivityDashBoardBinding.inflate(layoutInflater)
    }


    @Inject
    lateinit var localDataStore: LocalDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getUniversityEvent()

        binding.courseRegistration.setOnClickListener {
            startActivity(CourseRegistrationActivity.newIntent(this))
        }

        binding.gpaCalculator.setOnClickListener {
            startActivity(GpaCalculatorActivity.newIntent(this))
        }

        binding.parkingPlans.setOnClickListener {
            startActivity(ParkingPlanActivity.newIntent(this))
        }

        binding.mealPlans.setOnClickListener {
            startActivity(MealPlanActivity.newIntent(this))
        }

        binding.profile.setOnClickListener {
            startActivity(ProfileActivity.newIntent(this))
        }

        binding.chat.setOnClickListener {
            startActivity(CourseRecommendationActivity.newIntent(this))
        }

        binding.mapCard.setOnClickListener {
            startActivity(MapsActivity.newIntent(this))
        }

        binding.scheduleAppointment.setOnClickListener {
            startActivity(StudentScheduleAppointment.newIntent(this))
        }
    }


    private fun getUniversityEvent() {
        universityEventViewModel.getUniversityEvent()

        lifecycleScope.launch {
            universityEventViewModel.universityEvent.collect { universityEvent ->
                when (universityEvent) {
                    is NetworkResult.Loading -> {
//                        showToast("Loading")
                    }

                    is NetworkResult.Success -> {
                        binding.eventsLayout.show()
                        binding.eventsViewPager.adapter =
                            universityEvent.body?.reversed()?.let {list ->
                                UniversityEventAdapter(list) {
                                    val intent = UniversityEventActivity.newIntent(this@DashBoardActivity)
                                    intent.putExtra("image", it.imageUrl)
                                    intent.putExtra("name", it.name)
                                    intent.putExtra("description", it.description)
                                    intent.putExtra("date", it.date)
                                    intent.putExtra("time", it.time)
                                    intent.putExtra("location", it.location)
                                    startActivity(intent)
                                }
                            }
                        binding.dotsIndicator.attachTo(binding.eventsViewPager)
                    }

                    is NetworkResult.Error -> {
                        showToast(universityEvent.errorMessage)
                    }

                    else -> {}
                }

            }
        }

    }

    companion object {
        fun newIntent(activity: AppCompatActivity): Intent {
            return Intent(activity, DashBoardActivity::class.java)
        }
    }

    private lateinit var mMap: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val tamucc = LatLng(27.7121, -97.3254)
        mMap.addMarker(MarkerOptions().position(tamucc).title("Tamucc Campus"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tamucc))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tamucc, 16f))
    }
}