package com.adrpien.musicplayerapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.adrpien.musicplayerapp.App.Companion.PLAYER_CHANNEL

class PlayerService: Service() {

    private val intentRequestCode: Int = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!isPlaying) {
            // Creating pending intent
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                intentRequestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Creating notification for service
            val notification = NotificationCompat.Builder(this, PLAYER_CHANNEL)
                .setContentIntent(pendingIntent)
                .setContentTitle("Music App")
                .setContentText("Song name")
                .setSmallIcon(R.drawable.app_notification_icon)
                .build()


            // Creating media player - what service has to do
            mediaPlayer = MediaPlayer.create(this, R.raw.taco_hemingway_europa)
            mediaPlayer.start()
            isPlaying = true

            // Starting Service
            startForeground(0, notification)
        }

        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        isPlaying = false
    }
}
