package com.antoinetrouve.theendormap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import timber.log.Timber

private const val REQUEST_PERMISSION_LOCATION_START_UPDATE = 2
private const val REQUEST_CHECK_SETTINGS = 1

class MainActivity : AppCompatActivity() {

   private lateinit var locationLiveData: LocationLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationLiveData = LocationLiveData(this).apply {
            observe(this@MainActivity, Observer { handleLocationData(it!!) })
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

    private fun handleLocationData(locationData: LocationData) {
        if (handleLocationException(locationData.exception)) return

        Timber.i("last location from LIVE DATA ${locationData.location}")
    }

    private fun handleLocationException(exception: Exception?): Boolean {
        exception ?: return false
        Timber.e(exception, "handleLocationException")
        when (exception) {
            is SecurityException -> {
                checkLocationPermission(REQUEST_PERMISSION_LOCATION_START_UPDATE)
            }
            is ResolvableApiException -> {
                exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
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
