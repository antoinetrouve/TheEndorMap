package com.antoinetrouve.theendormap.geofence

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

class GeofenceIntentService : IntentService("EndorGeofenceIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        Timber.d("onHandleIntent: $intent")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        // Check if event has any error
        if (geofencingEvent.hasError()) {
            Timber.e("Error in geofence Intent ${geofencingEvent.errorCode}")
            return
        }

        // Check the transition type
        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Timber.e("Unhandled geofencing transition : $geofenceTransition")
        }

        // Check the geofence trigger
        if (geofencingEvent.triggeringGeofences == null) {
            Timber.w("Empty triggering geofences, nothing to do")
            return
        }

        geofencingEvent.triggeringGeofences.forEach {
            if (it.requestId == GEOFENCE_ID_MORDOR) {
                Timber.w("ENTERING MORDOR")
            }
        }

    }
}