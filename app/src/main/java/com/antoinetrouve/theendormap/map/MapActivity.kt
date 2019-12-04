package com.antoinetrouve.theendormap.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.antoinetrouve.theendormap.R
import com.antoinetrouve.theendormap.geofence.GEOFENCE_ID_MORDOR
import com.antoinetrouve.theendormap.geofence.GeofenceManager
import com.antoinetrouve.theendormap.location.LocationData
import com.antoinetrouve.theendormap.location.LocationLiveData
import com.antoinetrouve.theendormap.poi.MOUNT_DOOM
import com.antoinetrouve.theendormap.poi.Poi
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

private const val REQUEST_PERMISSION_LOCATION_START_UPDATE = 2
private const val REQUEST_CHECK_SETTINGS = 1

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationLiveData: LocationLiveData
    private lateinit var userMarker: Marker
    private lateinit var map: GoogleMap
    private lateinit var geofenceManager: GeofenceManager
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
        geofenceManager = GeofenceManager(this)

        locationLiveData = LocationLiveData(this).apply {
            observe(this@MapActivity, Observer { handleLocationData(it!!) })
        }

        viewModel.getUiState().observe(this, Observer { updateUiState(it!!) })
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

        with(map) {
            // Add a map style (generated with https://mapstyle.withgoogle.com/)
            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapActivity, R.raw.maps_style))
            // Custom a map info windows
            setInfoWindowAdapter(EndorInfoWindowAdapter(this@MapActivity))

            // On info window click event
            setOnInfoWindowClickListener {
                showPoiDetail(it.tag as Poi)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.generatePois -> {
                refreshPoisFromCurrentLocation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshPoisFromCurrentLocation() {
        // Delete all Geofence task
        geofenceManager.removeAllGeofences()

        // Delete all map
        map.clear()

        // reload user pois
        viewModel.loadPois(userMarker.position.latitude, userMarker.position.longitude)
    }

    private fun showPoiDetail(poi: Poi) {
        if (poi.detailUrl.isEmpty()) return

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(poi.detailUrl))
        startActivity(intent)
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
                state.userPoi?.let { poi ->
                    userMarker = addPoiToMapMarker(poi, map)
                }
                state.pois?.let { pois ->
                    pois.forEach {
                        addPoiToMapMarker(it, map)
                        if (it.title == MOUNT_DOOM) {
                            geofenceManager.createGeofence(it, 12000.0f, GEOFENCE_ID_MORDOR)
                        }
                    }
                }
            }
        }
    }

    private fun handleLocationData(locationData: LocationData) {
        if (handleLocationException(locationData.exception)) return
        locationData.location?.let {
            val latLng = LatLng(it.latitude, it.longitude)

            if (firstLocation && ::map.isInitialized) {
                // center camera on user's position
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9f))

                firstLocation = false
                viewModel.loadPois(it.latitude, it.longitude)
            }

            if (::userMarker.isInitialized) {
                userMarker.position = latLng
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

/**
 * Create a Marker according to Poi given in parameter
 * @param poi source de donnée
 * @param map Google map instance
 * @return Marker
 */
private fun addPoiToMapMarker(poi: Poi, map: GoogleMap) : Marker {
    val options = MarkerOptions()
        .position(LatLng(poi.latitude, poi.longitude))
        .title(poi.title)
        .snippet(poi.description)

    // Config an icon marker or an icon color according to poi
    if (poi.iconId > 0) {
        options.icon(BitmapDescriptorFactory.fromResource(poi.iconId))
    } else if (poi.iconColor != 0) {
        val hue = when (poi.iconColor) {
            Color.BLUE -> BitmapDescriptorFactory.HUE_AZURE
            Color.GREEN -> BitmapDescriptorFactory.HUE_GREEN
            Color.YELLOW -> BitmapDescriptorFactory.HUE_YELLOW
            Color.RED -> BitmapDescriptorFactory.HUE_RED
            else ->BitmapDescriptorFactory.HUE_RED
        }
        options.icon(BitmapDescriptorFactory.defaultMarker(hue))
    }

    val marker = map.addMarker(options)
    marker.tag = poi

    return marker
}
