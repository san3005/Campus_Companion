package com.maggie.rapidsync

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.maggie.rapidsync.adapters.LocationAdapter
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.databinding.ActivityMapsBinding
import com.maggie.rapidsync.model.pojo.MapLocation
import com.maggie.rapidsync.viewmodel.MapLocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel: MapLocationViewModel by viewModels()

    private var locations = listOf<MapLocation>()

    private val locationAdapter by lazy {
        LocationAdapter {
            val location = locations[it]
            val googleMapsIntentUri = Uri.parse(
                "google.navigation:q=${location.latitude},${location.longitude}"
            )

            val mapIntent = Intent(Intent.ACTION_VIEW, googleMapsIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.resultsRecyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.resultsRecyclerView.adapter = locationAdapter


        lifecycleScope.launch {
            viewModel.mapLocations.collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        // Show loading
                    }

                    is NetworkResult.Error -> {
                        // Show error
                    }

                    is NetworkResult.Success -> {
                        // Show success
                        it.body?.let { it1 ->
                            showMapLocations(it1)
                            locations = it1
                        }
                    }

                    else -> {}
                }
            }
        }
        //                locationAdapter.updateData(locations.filter { it.contains(query!!, true) })

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener,
            SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    locationAdapter.updateData(listOf())
                    return false
                }
                locationAdapter.updateData(locations.filter { it.name.contains(newText, true) }
                    .map { it.name })
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) {
                    locationAdapter.updateData(listOf())
                    return false
                }
                locationAdapter.updateData(locations.filter { it.name.contains(query, true) }
                    .map { it.name })
                return false
            }

        })

    }

    private fun showMapLocations(body: List<MapLocation>) {
        for (location in body) {
            val latLng = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
            mMap.addMarker(MarkerOptions().position(latLng).title(location.name))
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        viewModel.fetchMapLocations()

        // Add a marker in Sydney and move the camera
        val tamucc = LatLng(27.7121, -97.3254)
        mMap.addMarker(MarkerOptions().position(tamucc).title("Tamucc Campus"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tamucc))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tamucc, 16f))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                listOf(Manifest.permission.ACCESS_FINE_LOCATION).toTypedArray(),
                1
            );
            return
        }
        googleMap.isMyLocationEnabled = true

        mMap.setOnMarkerClickListener(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                }
            }
        }
    }

    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, MapsActivity::class.java)
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        // Create a Uri from an intent string. Use the result to create an Intent.
        val googleMapsIntentUri = Uri.parse(
            "google.navigation:q=${p0.position.latitude},${p0.position.longitude}"
        )

        val mapIntent = Intent(Intent.ACTION_VIEW, googleMapsIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        return true
    }
}