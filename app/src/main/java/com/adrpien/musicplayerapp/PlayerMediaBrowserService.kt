package com.adrpien.musicplayerapp

import android.content.Intent
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_MEDIA_PAUSE
import androidx.media.MediaBrowserServiceCompat

private const val MEDIA_ROOT_ID = "media_root_id"
private const val EMPTY_MEDIA_ROOT_ID = "empty_media_root_id"
private const val MEDIA_SESSION_TAG = "media_session_tag"

class PlayerMediaBrowserService: MediaBrowserServiceCompat() {

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false


    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()


        // Creating mediaSession
        mediaSession = MediaSessionCompat(baseContext, MEDIA_SESSION_TAG).apply {

            // DEPRECATED
            // Enabling callback from MediaButtons and TransportControls
            // setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            /* Playback state for a MediaSession. This includes a state like PlaybackState#STATE_PLAYING,
               the current playback position, and the current control capabilities */

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(object: MediaSessionCompat.Callback(){
                // TODO MediaSessionCompat.Callback to implement
            })

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }


    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {

        val allowBrowsing = true
        // (Optional) Control the level of access f.e. for the specified package name.
        // Need to write your own logic to do this.
        return if (allowBrowsing) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            MediaBrowserServiceCompat.BrowserRoot(MEDIA_ROOT_ID, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            MediaBrowserServiceCompat.BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
        }

    }
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {

        //  Browsing not allowed
        if (EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }

        // Assume for example that the music catalog is already loaded/cached.

        val playList = mutableListOf<MediaBrowserCompat.MediaItem>()

        // Check if this is the root menu:
        if (MEDIA_ROOT_ID == parentId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(playList)    }

}

