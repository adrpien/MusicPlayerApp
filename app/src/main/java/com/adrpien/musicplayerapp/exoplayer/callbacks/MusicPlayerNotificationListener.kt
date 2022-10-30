package com.adrpien.musicplayerapp.exoplayer.callbacks

import android.app.Notification
import android.app.Service
import android.content.Intent
import androidx.core.content.ContextCompat
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.exoplayer.PlayerMediaBrowserService
import com.adrpien.musicplayerapp.other.Constants.MUSIC_NOTIFICATION_CHANNEL_ID
import com.google.android.exoplayer2.ui.PlayerNotificationManager

// In this class we set what we want to do when our notification is canceled or posted
class  MusicPlayerNotificationListener(private val musicService: PlayerMediaBrowserService):  PlayerNotificationManager.NotificationListener{
    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
            isForegroundService = false
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                startForeground(R.string.music_notification_channel_id, notification)
                isForegroundService = true
            }

        }
    }
}