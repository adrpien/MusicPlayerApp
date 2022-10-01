package com.adrpien.musicplayerapp

import PlayerNotificationReceiver
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adrpien.musicplayerapp.App.Companion.PLAYER_CHANNEL

class PlayerService: Service() {

    // MediaPlayer
    private lateinit var mediaPlayer: MediaPlayer

    // Is music playing
    private var isPlaying: Boolean = false

    // IBinder
    private val binder: IBinder = PlayerServiceBinder()

    // Song URI
    private lateinit var songUri: Uri


    // Broadcast receiver
    private lateinit var playerNotificationReceiver: PlayerNotificationReceiver

    private val intentRequestCode: Int = 0

    override fun onCreate() {
        super.onCreate()

        // Creating media player - service main task
        //mediaPlayer = MediaPlayer.create(this, R.raw.taco_hemingway_europa)
        // Play next song automatically when previous is finished
        /*mediaPlayer.setOnCompletionListener {
            nextSong()
        }*/
    }

    override fun onBind(intent: Intent?): IBinder? {
       return binder
    }

    // onStartCommand implementation
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Getting song
        // songUri = Uri.parse(intent!!.extras!!.getString("song"))

        // Broadcast receiver
        playerNotificationReceiver = PlayerNotificationReceiver()

        // Registering broadcast receiver
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.PLAY_ACTION)))
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.PAUSE_ACTION)))
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.BACK_ACTION)))
        registerReceiver(playerNotificationReceiver, IntentFilter(getString(R.string.NEXT_ACTION)))


        // Creating notification
        val notification = createNotification()


        mediaPlayer = MediaPlayer.create(this, R.raw.taco_hemingway_europa)


        if (intent != null){
            when(intent.action){
                getString(R.string.PLAY_ACTION) -> {
                  playMusic()
                }
                getString(R.string.PAUSE_ACTION) -> {
                    pauseMusic()
                }
                getString(R.string.NEXT_ACTION) -> {
                    playMusic()
                }
                getString(R.string.BACK_ACTION) -> {
                    playMusic()
                }
            }

        }

        // Starting Service
        startForeground(1, notification)

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

    private fun nextSong(){

        // TODO Next song fun to implement
        mediaPlayer.stop()
        mediaPlayer.setDataSource(applicationContext, songUri)
        if(isPlaying) {
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    private fun changeSong(songUri: Uri){
        mediaPlayer.stop()
        mediaPlayer.setDataSource(applicationContext, songUri)
        if(isPlaying) {
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    private fun playMusic(){
        mediaPlayer.start()
    }

    private fun pauseMusic(){
        mediaPlayer.pause()
    }

    inner class PlayerServiceBinder: Binder(){
        fun getService(): PlayerService{
            return this@PlayerService
        }
    }

    private fun createNotification(): Notification {
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
        val nextPendingIntent =
            PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        // Pending intent with broadcast for custom view back button on  click
        val backIntent = Intent(getString(R.string.PAUSE_ACTION))
        backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val backPendingIntent =
            PendingIntent.getBroadcast(this, 0, backIntent, PendingIntent.FLAG_IMMUTABLE)

        // Pending intent with broadcast for custom view back button on  click
        val playIntent = Intent(getString(R.string.PLAY_ACTION))
        playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val playPendingIntent =
            PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)



        // Remote view
        val playerNotificationLayout = RemoteViews(packageName, R.layout.player_small_notification)

        val playerNotificationLayoutExpanded =
            RemoteViews(packageName, R.layout.player_large_notification)

        // Building notification with custom view
        val notificationBuilder = NotificationCompat.Builder(this, PLAYER_CHANNEL)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.app_notification_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(playerNotificationLayout)
            .setSilent(true)

        //                .setCustomBigContentView(playerNotificationLayoutExpanded)
        //                .setContentTitle("Taco Hemingway")
        //                .setContentText("Europa")

        // Setting custom view buttons on click reaction
        playerNotificationLayout.setOnClickPendingIntent(
            R.id.notificationPlayButton,
            playPendingIntent
        )


        playerNotificationLayout.setOnClickPendingIntent(
            R.id.notificationBackButton,
            backPendingIntent
        )
        playerNotificationLayout.setOnClickPendingIntent(
            R.id.notificationNextButton,
            nextPendingIntent
        )

        // Notification manager
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Creating notification
        val notification = notificationBuilder.build()

        // Post a notification to be shown in the status bar.
        notificationManager.notify(1, notification)
        return notification
    }
}
