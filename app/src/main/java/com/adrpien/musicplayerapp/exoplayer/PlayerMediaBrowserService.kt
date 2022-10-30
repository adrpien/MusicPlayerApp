package com.adrpien.musicplayerapp.exoplayer

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.adrpien.musicplayerapp.exoplayer.callbacks.MusicPlaybackPreparer
import com.adrpien.musicplayerapp.exoplayer.callbacks.MusicPlayerEventListener
import com.adrpien.musicplayerapp.exoplayer.callbacks.MusicPlayerNotificationListener
import com.adrpien.musicplayerapp.other.Constants.SERVICE_TAG
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class PlayerMediaBrowserService: MediaBrowserServiceCompat() {

    @Inject
    lateinit var defaultDataSourceFactory: DefaultDataSource.Factory

    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

    @Inject
    lateinit var exoPlayer: ExoPlayer

    var isForegroundService = false

    private var currentlyPlayingSong: MediaMetadataCompat? = null

    // Coroutine initialization
    private val serviceJob = Job()
    private val serviceScoped = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var musicNotificationManager: MusicNotificationManager

    // MediaSession
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    override fun onCreate() {
        super.onCreate()

        // Getting PendingIntent to our activity
        val activityPendingIntent = packageManager?.getLaunchIntentForPackage(packageName)?. let {
            PendingIntent.getActivity(this,0, it, 0)
        }

        // MediaSession initialization
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityPendingIntent)
            isActive = true
        }

        // Setting sessionToken property of our service as sessionToken property of its MediaSession instance
        sessionToken = mediaSession.sessionToken


        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)) {
            // This lambda toggles when song switches
        }

        // MusicPlaybackPreparer initialization
        val musicPlaybackPreparer = MusicPlaybackPreparer(firebaseMusicSource) {
            currentlyPlayingSong = it
            prepareExoplayer(
                firebaseMusicSource.songsMetadata,
                it,
                true
            )
        }

        // MediaSessionConnector initalization
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setPlayer(exoPlayer)

        exoPlayer.addListener(MusicPlayerEventListener(this))
        musicNotificationManager.showNotification(exoPlayer)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Make sure to cancel coroutine when service dies (to prevent memory leakage)
        serviceScoped.cancel()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
         TODO("Not yet implemented")
    }

    private fun prepareExoplayer(
        songs: List<MediaMetadataCompat>,
        songToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ){
        val songIndex = if(currentlyPlayingSong == null) 0  else songs.indexOf(songToPlay)
        exoPlayer.setMediaSource(firebaseMusicSource.asMediaSource(defaultDataSourceFactory))
        exoPlayer.prepare()
        exoPlayer.seekTo(songIndex, 0L)
        exoPlayer.playWhenReady = playNow
        }
    }

}

