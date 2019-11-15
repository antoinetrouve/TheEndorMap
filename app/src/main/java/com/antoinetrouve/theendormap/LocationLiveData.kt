package com.antoinetrouve.theendormap

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception

data class LocationData(val location: Location? = null, val exception: Exception? = null)

class LocationLiveData(context: Context) : LiveData<LocationData>() {

    // app context which is always available
    private val appContext = context.applicationContext
    private val fusedLocationClient : FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    // Use to track the first LiveData subscriber to send the last known location immediately
    private var firstSubscriber = true

    override fun onActive() {
        super.onActive()
        if (firstSubscriber) {
            requestLastLocation()
            firstSubscriber = false
        }
    }

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