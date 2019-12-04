package com.antoinetrouve.theendormap.geofence

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.graphics.DrawFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.antoinetrouve.theendormap.App
import com.antoinetrouve.theendormap.R
import com.antoinetrouve.theendormap.map.MapActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

private const val NOTIFICATION_ID_MORDOR = 0

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
                sendNotification(geofenceTransition)
                Timber.w("ENTERING MORDOR")
            }
        }

    }

    private fun sendNotification(transitionType: Int) {
        val text: String
        val drawable: Drawable
        val title: String = when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                text = "Be careful... Sauron is always watching..."
                drawable = ContextCompat.getDrawable(this, R.drawable.sauroneye)!!
                "You entered the Mordor !"

            }
            else -> {
                text = "You can breath now.. But where is the One Ring ?"
                drawable = ContextCompat.getDrawable(this, R.drawable.mordorgate)!!
                "You left the Mordor"
            }
        }

        val bitmap = (drawable as BitmapDrawable).bitmap

        val intent = Intent(this, MapActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID_MORDOR, builder.build())
    }
}