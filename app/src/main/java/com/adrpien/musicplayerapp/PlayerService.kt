package com.adrpien.musicplayerapp

import PlayerNotificationReceiver
import android.R.id.closeButton
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.adrpien.musicplayerapp.App.Companion.PLAYER_CHANNEL
import com.adrpien.musicplayerapp.App.Companion.mediaPlayer


class PlayerService: Service() {

    private val binder = PlayerServiceBinder()
    private var song: Int = 0

    var songDuration: Long = 0

    private lateinit var playerNotificationReceiver: PlayerNotificationReceiver

    private val intentRequestCode: Int = 0
    // private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var isPlaying: Boolean = false


    override fun onBind(intent: Intent?): IBinder? {
       return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Broadcast receiver

        playerNotificationReceiver = PlayerNotificationReceiver()
        // Registering broadcast receiver
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.PLAY_ACTION)))
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.PAUSE_ACTION)))
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.BACK_ACTION)))
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.NEXT_ACTION)))


        song = intent?.getIntExtra("song", 0) ?: R.raw.taco_hemingway_europa

        // onStartCommand implementation
        if (!isPlaying) {

            // Creating pending intent for notification
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                intentRequestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Pending intent with broadcast for custom view back button on  click
            val nextIntent = Intent(getString(R.string.NEXT_ACTION))
            nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

            // Pending intent with broadcast for custom view back button on  click
            val backIntent = Intent(getString(R.string.BACK_ACTION))
            backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val backPendingIntent = PendingIntent.getBroadcast(this, 0, backIntent, PendingIntent.FLAG_IMMUTABLE)

            // Pending intent with broadcast for custom view back button on  click
            val playIntent = Intent(getString(R.string.PLAY_ACTION))
            playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)

            // Remote view
            val playerNotificationLayout = RemoteViews(packageName, R.layout.player_small_notification)

            val playerNotificationLayoutExpanded = RemoteViews(packageName, R.layout.player_large_notification)

            // Building notification with custom view
            val notificationBuilder = NotificationCompat.Builder(this, PLAYER_CHANNEL)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_notification_icon)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(playerNotificationLayout)
//                .setCustomBigContentView(playerNotificationLayoutExpanded)
//                .setContentTitle("Taco Hemingway")
//                .setContentText("Europa")

            // Setting custom view button on click reaction
            playerNotificationLayout.setOnClickPendingIntent(R.id.notificationPlayButton, playPendingIntent)
            playerNotificationLayout.setOnClickPendingIntent(R.id.notificationBackButton, backPendingIntent)
            playerNotificationLayout.setOnClickPendingIntent(R.id.notificationNextButton, nextPendingIntent)

            // Notification manager
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Creating notification
            val  notification = notificationBuilder.build()

            // Post a notification to be shown in the status bar.
            notificationManager.notify(1, notification)


            // Creating media player and starting music - service main task
            mediaPlayer = MediaPlayer.create(this, song)
            //mediaPlayer.start()
            //isPlaying = true
            //songDuration = mediaPlayer.duration.toLong()


            // Starting Service
            startForeground(1, notification)
        }

        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop playing music and set isPlaying to false
        mediaPlayer.stop()
        isPlaying = false

        // Unregistering broadcast receiver
        unregisterReceiver(playerNotificationReceiver)
    }

    inner class PlayerServiceBinder: Binder(){
        fun getService(): PlayerService{
            return this@PlayerService
        }

    }
}
