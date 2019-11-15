package com.antoinetrouve.theendormap

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import timber.log.Timber
import kotlin.Exception

data class LocationData(val location: Location? = null, val exception: Exception? = null)

class LocationLiveData(context: Context) : LiveData<LocationData>() {

    // app context which is always available
    private val appContext = context.applicationContext

    // Location client
    private val fusedLocationClient : FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    // Location request settings
    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // Location response
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            locationResult.locations.forEach { location ->
                Timber.d("location update $location")
                value = LocationData(location = location)
            }
        }
    }

    // Use to track the first LiveData subscriber
    // to send the last known location immediately
    private var firstSubscriber = true

    override fun onActive() {
        super.onActive()
        if (firstSubscriber) {
            requestLastLocation()
            requestLocation()
            firstSubscriber = false
        }
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        firstSubscriber = true
    }

    /**
     * Start a request to get user's location or expose exception.
     */
    fun startRequestLocation() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = LocationServices.getSettingsClient(appContext).run {
            checkLocationSettings(builder.build())
        }

        task.addOnSuccessListener { locationSettingsResponse ->
            Timber.i("Location settings satisfied. Init location request here")
            requestLocation()
        }

        task.addOnFailureListener { exception ->
            Timber.e(exception, "Failed to modify location settings")
            value = LocationData(exception = exception)
        }
    }

    /**
     * Send request location.
     */
    private fun requestLocation() {
        try {
            // request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (exception: SecurityException) {
            value = LocationData(exception = exception)
        }
    }

    /**
     * Send request to get last position
     */
    private fun requestLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                value = LocationData(location = location)
            }

            fusedLocationClient.lastLocation.addOnFailureListener { exception ->
                value = LocationData(exception = exception)
            }
        } catch (e: SecurityException) {
            value = LocationData(exception = e)
        }
    }
}