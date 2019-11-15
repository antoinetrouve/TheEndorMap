package com.antoinetrouve.theendormap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import timber.log.Timber

private const val REQUEST_PERMISSION_LAST_LOCATION = 1
private const val REQUEST_CHECK_SETTINGS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // get location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateLastLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return
        }
        when (requestCode) {
            REQUEST_PERMISSION_LAST_LOCATION -> updateLastLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
           REQUEST_CHECK_SETTINGS -> startLocationUpdate()
        }
    }

    private fun startLocationUpdate() {
        Timber.i("Start location update")
    }

    private fun updateLastLocation() {
        Timber.d("update location")
        if (!checkLocationPermission()) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            Timber.i("Last location : $location")
        }

        createLocationRequest()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            Timber.i("Location settings satisfied. Init location request here")
            startLocationUpdate()
        }

        task.addOnFailureListener { exception ->
            Timber.e(exception, "Failed to modify location settings")
            if (exception is ResolvableApiException) {
                exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LAST_LOCATION
            )
            return false
        }
        return true
    }
}
