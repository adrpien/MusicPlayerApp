package com.adrpien.musicplayerapp.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.adrpien.musicplayerapp.R
import com.adrpien.musicplayerapp.other.Constants.MUSIC_NOTIFICATION
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {

    // PlayerNotificationManager is custom class from Exoplayer that can manage notification
    private val notificationManager: PlayerNotificationManager

    init{

        val mediaController = MediaControllerCompat(context, sessionToken)

        notificationManager =
            PlayerNotificationManager.Builder(
                context,
                R.string.music_notification_channel_id,
                MUSIC_NOTIFICATION
            )
                .setChannelNameResourceId(R.string.music_notification_channel)
                .setChannelDescriptionResourceId(R.string.notification_channel_description)
                .setMediaDescriptionAdapter(DescriptionAdapter(mediaController)) // Media controller is used to controll media
                .setNotificationListener(notificationListener)
                .setSmallIconResourceId(R.drawable.app_notification_icon) // small icon in upper bar
                .build()
                .apply {
                   setMediaSessionToken(sessionToken) // Gives to PlayerNotificationManager access to MediaSession
                }


    }

    fun showNotification(player: Player){
        // Setting the player starts a notification immediately unless the player
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(val mediaController: MediaControllerCompat) : PlayerNotificationManager.MediaDescriptionAdapter {

        // Return title of current song
        override fun getCurrentContentTitle(player: Player): CharSequence {
        return mediaController.metadata.description.title.toString() // MediaMetaDataCompat is metadata type
        }

        // Creates Pending intent which lead to PlayerFragment
        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        // Returns current subtitle
        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata.description.subtitle.toString()
        }

        /*
        This function can return null, but loading asynchronously icon with glide,
        triggers callback, when the loading is finished (fun onResourceReady runs)
         */

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
        Glide.with(context).asBitmap()
            .load(mediaController.metadata.description.iconUri)
            .into(object: CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Update notification icon
                    callback.onBitmap(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) = Unit
            })
            return null
        }


    }

}