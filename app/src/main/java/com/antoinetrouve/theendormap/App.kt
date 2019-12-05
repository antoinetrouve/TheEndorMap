package com.antoinetrouve.theendormap

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import timber.log.Timber

class App : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID  = "EndorMap"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        createNotificationChannel()
    }

    /**
     * Create a specific channel Notification
     * to enable Notification for device > Oreo (Version 8 SDK 26)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val name = "Endor Map Notifications"
        val descriptionText = "Be notified when you enter Middle Earth special areas."
        val weight = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, weight).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}