package com.antoinetrouve.theendormap.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.antoinetrouve.theendormap.poi.Poi
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import timber.log.Timber

const val GEOFENCE_ID_MORDOR = "Mordor"

class GeofenceManager(context: Context) {
    private val appContext = context.applicationContext
    private val geofencingClient = LocationServices.getGeofencingClient(appContext)
    private val geofences = mutableListOf<Geofence>()

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(appContext, GeofenceIntentService::class.java)
        PendingIntent.getService(
            appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * Configure geofence zone.
     * @param poi The point of Interest attached to geofence event
     * @param radiusMeter The radius of geofence (in meter)
     */
    fun createGeofence(poi: Poi, radiusMeter: Float, requestId: String) {
        Timber.d("creating geofence at coordinates ${poi.latitude}, ${poi.longitude}")
        geofences.add(
            Geofence.Builder()
                .setRequestId(requestId)
                .setExpirationDuration(10 * 60 * 100) // 10 min or Geofence.NEVER_EXPIRE for no limit duration.
                .setCircularRegion(poi.latitude, poi.longitude, radiusMeter)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build()
        )

        val task = geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)
        task.addOnSuccessListener {
            Timber.i("Geofence added")
        }

        task.addOnFailureListener {exception ->
            Timber.e(exception, "Cannot add geofence")

        }
    }

    /**
     * Delete all geofences from client.
     */
    fun removeAllGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent)
        geofences.clear()
    }

    /**
     * Create geofencing request
     * @return GeofencingRequest
     */
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()
    }
}