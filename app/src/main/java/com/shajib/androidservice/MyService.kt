package com.shajib.androidservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


/**
 * @author Shajib
 * @since Aug 11, 2024
 **/
class MyService : Service() {

    companion object {
        val START_SERVICE = "START_SERVICE"
        val STOP_SERVICE = "STOP_SERVICE"
        val FOREGROUND_SERVICE = "FOREGROUND_SERVICE"

        const val TAG = "MyService"
    }

    var isForegroundService = false

    val CHANNEL_ID: String = "CHANNEL_ID"

    inner class LocalBinder : Binder() { // pros and cons of inner classes
        fun getService(): MyService = this@MyService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START_SERVICE -> {
                showToast("Service Started")
            }

            STOP_SERVICE -> {
                stopThisService()
            }

            FOREGROUND_SERVICE -> {
                doForegroundThings()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun doForegroundThings() {
        showToast("Going to Foreground")
        createNotificationChannel()
        isForegroundService = true
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My Foreground Service [Notification]")
            .setContentText("This is a foreground service [Notification]")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0, Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
                notify(4, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "My Custom Channel"
            val channelDescription = "My Custom Notification Channel"
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun stopThisService() {
        showToast("Service Stopped")
        try {
            stopForeground(STOP_FOREGROUND_DETACH)
            isForegroundService = false
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val binder = LocalBinder()
    override fun onBind(p0: Intent?): IBinder {
        return binder
    }
}