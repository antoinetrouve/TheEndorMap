package com.antoinetrouve.theendormap.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.antoinetrouve.theendormap.R
import com.antoinetrouve.theendormap.location.LocationData
import com.antoinetrouve.theendormap.location.LocationLiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

private const val REQUEST_PERMISSION_LOCATION_START_UPDATE = 2
private const val REQUEST_CHECK_SETTINGS = 1

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationLiveData: LocationLiveData
    private lateinit var map: GoogleMap
    private var firstLocation: Boolean = true

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(this)[MapViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create map options
        val mapOptions = GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NORMAL)
            .zoomControlsEnabled(true)
            .zoomGesturesEnabled(true)

        // Create the map fragment
        val mapFragment = SupportMapFragment.newInstance(mapOptions)
        mapFragment.getMapAsync(this)

        supportFragmentManager.beginTransaction().replace(R.id.content, mapFragment).commit()

        locationLiveData = LocationLiveData(this).apply {
            observe(this@MapActivity, Observer { handleLocationData(it!!) })
        }

        viewModel.getUiState().observe(this, Observer { updateUiState(it!!) })
    }

    private fun updateUiState(state: MapUiState) {
        Timber.w("state= ${state::class.java.simpleName}")
        when (state) {
            MapUiState.Loading -> {
                loadingProgressBar.show()
            }
            is MapUiState.Error -> {
                loadingProgressBar.hide()
                Toast.makeText(
                    this,
                    "error : ${state.errorMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is MapUiState.PoiReady -> {
                loadingProgressBar.hide()
                state.userPoi?.let {

                }
                state.pois?.let {

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // if no result or no granted permission, do nothing
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) return

        when (requestCode) {
            REQUEST_PERMISSION_LOCATION_START_UPDATE -> locationLiveData.startRequestLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> locationLiveData.startRequestLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Add a map style (generated with https://mapstyle.withgoogle.com/)
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.maps_style
            )
        )
    }

    private fun handleLocationData(locationData: LocationData) {
        if (handleLocationException(locationData.exception)) return
        locationData.location?.let {
            if (firstLocation && ::map.isInitialized) {
                // center camera on user's position
                val latLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9f))

                firstLocation = false
                viewModel.loadPois(it.latitude, it.longitude)
            }
        }
    }

    private fun handleLocationException(exception: Exception?): Boolean {
        exception ?: return false
        Timber.e(exception, "handleLocationException")
        when (exception) {
            is SecurityException -> {
                checkLocationPermission(REQUEST_PERMISSION_LOCATION_START_UPDATE)
            }
            is ResolvableApiException -> {
                exception.startResolutionForResult(
                    this,
                    REQUEST_CHECK_SETTINGS
                )
            }
        }
        return true
    }

    /**
     * Check location permission
     * @param requestCode
     * @return true if permission is granted,
     * false if not and request permission (cf. #[onRequestPermissionsResult])
     */
    private fun checkLocationPermission(requestCode: Int): Boolean {
        return if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission to the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode
            )
            false
        } else {
            true
        }
    }
}
