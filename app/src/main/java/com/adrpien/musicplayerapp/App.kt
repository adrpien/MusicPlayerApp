package com.adrpien.musicplayerapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App: Application() {

    companion object{
        const val  PLAYER_CHANNEL = "PLAYER CHANNEL"
    }

    override fun onCreate() {
        super.onCreate()

        // Use NotificationChannel if API 26 or higher
        // Build.VERSION_CODES.O - Oreo (API26)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // Creating notification channel
            val playerServiceChannel = NotificationChannel(
                PLAYER_CHANNEL,
                "",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Registering notification channel to the system with NotificationManager
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(playerServiceChannel)
        }

    }

}