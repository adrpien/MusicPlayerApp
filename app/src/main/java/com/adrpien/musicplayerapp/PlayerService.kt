package com.adrpien.musicplayerapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.adrpien.musicplayerapp.App.Companion.PLAYER_CHANNEL
import java.util.*
import kotlin.concurrent.timerTask

class PlayerService: Service() {

    var songDuration: Long = 0

    private val intentRequestCode: Int = 0
    private lateinit var mediaPlayer: MediaPlayer
    var isPlaying: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Creating pending intent for notification
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
            songDuration = mediaPlayer.duration.toLong()


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
