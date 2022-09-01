package com.adrpien.musicplayerapp

import android.R.id.closeButton
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.adrpien.musicplayerapp.App.Companion.PLAYER_CHANNEL


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

//            val backIntent = Intent(this, PlayerBroadcastReceiver::class.java)
//            backIntent.setAction(getString(R.string.back_action))
//            backIntent.putExtra(getString(R.string.back_intent_extra_id), 0)
//            val backPendingIntent = PendingIntent.getBroadcast(this, 0, backIntent, PendingIntent.FLAG_IMMUTABLE)
//
//            val nextIntent = Intent(this, PlayerBroadcastReceiver::class.java)
//            nextIntent.setAction(getString(R.string.next_action))
//            nextIntent.putExtra(getString(R.string.next_intent_extra_id), 0)
//            val nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)
//
//            val playIntent = Intent(this, PlayerBroadcastReceiver::class.java)
//            playIntent.setAction(getString(R.string.play_action))
//            playIntent.putExtra(getString(R.string.play_intent_extra_id), 0)
//            val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)


            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val nextIntent = Intent(getString(R.string.BACK_ACTION))
            nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

            val backIntent = Intent(getString(R.string.BACK_ACTION))
            backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val backPendingIntent = PendingIntent.getBroadcast(this, 0, backIntent, PendingIntent.FLAG_IMMUTABLE)

            val playIntent = Intent(getString(R.string.PLAY_ACTION))
            playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)

            val playerNotificationLayout = RemoteViews(packageName, R.layout.player_small_notification)
            val playerNotificationLayoutExpanded = RemoteViews(packageName, R.layout.player_large_notification)

            // Creating notification for service
            val notificationBuilder = NotificationCompat.Builder(this, PLAYER_CHANNEL)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_notification_icon)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(playerNotificationLayout)
//                .setCustomBigContentView(playerNotificationLayoutExpanded)
//                .setContentTitle("Taco Hemingway")
//                .setContentText("Europa")
//                .addAction(R.drawable.play_button, getString(R.string.play), playPendingIntent)
//                .addAction(R.drawable.next_button, getString(R.string.next), nextPendingIntent)
//                .addAction(R.drawable.back_button, getString(R.string.back), backPendingIntent)

            playerNotificationLayout.setOnClickPendingIntent(R.id.notificationPlayButton, playPendingIntent)
            playerNotificationLayout.setOnClickPendingIntent(R.id.notificationBackButton, backPendingIntent)
            playerNotificationLayout.setOnClickPendingIntent(R.id.notificationNextButton, nextPendingIntent)

            val  notification = notificationBuilder.build()
            notificationManager.notify(1, notification)


            // Creating media player - what service has to do
            mediaPlayer = MediaPlayer.create(this, R.raw.taco_hemingway_europa)
            mediaPlayer.start()
            isPlaying = true
            songDuration = mediaPlayer.duration.toLong()


            // Starting Service
            startForeground(1, notification)
        }

        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        isPlaying = false
    }
}
