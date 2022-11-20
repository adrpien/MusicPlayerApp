package com.adrpien.musicplayerapp.exoplayer

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.adrpien.musicplayerapp.exoplayer.callbacks.MusicPlaybackPreparer
import com.adrpien.musicplayerapp.exoplayer.callbacks.MusicPlayerEventListener
import com.adrpien.musicplayerapp.exoplayer.callbacks.MusicPlayerNotificationListener
import com.adrpien.musicplayerapp.other.Constants.MEDIA_ROOT_ID
import com.adrpien.musicplayerapp.other.Constants.NETWORK_ERROR
import com.adrpien.musicplayerapp.other.Constants.SERVICE_TAG
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayerMediaBrowserService: MediaBrowserServiceCompat() {

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    @Inject
    lateinit var defaultDataSourceFactory: DefaultDataSource.Factory

    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

    @Inject
    lateinit var exoPlayer: ExoPlayer

     var isForegroundService = false

    private var isPlayerInitialized = false

    private var currentlyPlayingSong: MediaMetadataCompat? = null

    //private lateinit var musicServiceConnection: MusicServiceConnection

    // Coroutine initialization
    private val serviceJob = Job()
    private val serviceScoped = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var musicNotificationManager: MusicNotificationManager

    /*
    Media sessions are an integral link between the Android platform and media apps.
    Not only does it inform Android that media is playing — so that
    it can forward media actions into the correct session —
    but it also informs the platform what is playing and how it can be controlled.
     */
    // MediaSession
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    companion object {
        var songDuration = 0L
        private set // we can change only from the class but read from any
    }

    override fun onCreate() {
        super.onCreate()

        // Fetch the data immediately after service created
        serviceScoped.launch {
            firebaseMusicSource.fetchMetaData()
        }

        // Getting PendingIntent to our activity
        val activityPendingIntent = packageManager?.getLaunchIntentForPackage(packageName)?. let {
            PendingIntent.getActivity(this,0, it, FLAG_IMMUTABLE)
        }
        // MediaSession initialization
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityPendingIntent)
            isActive = true
        }

        // Setting sessionToken property of our service as sessionToken property of its MediaSession instance
        sessionToken = mediaSession.sessionToken

        /*// MediaSessionConnector initialization
        mediaSessionConnector = MediaSessionConnector(mediaSession)*/

        // Initializing MusicNotificationManager
        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)) {
            // This lambda toggles when song switches
            songDuration = exoPlayer.duration
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
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)
        musicPlayerEventListener =  MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Make sure to cancel coroutine when service dies (to prevent memory leakage)
        serviceScoped.cancel()
        // Remove Listener to prevent memory leaks
        exoPlayer.removeListener(musicPlayerEventListener)
        // Releases resources of exoplayer
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // If there is a need to create some  client verificiation, this is right place
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when(parentId) {
            MEDIA_ROOT_ID -> {
                val resultSent = firebaseMusicSource.whenReady { isInitialized ->
                    if (isInitialized){
                        // Send the result back to the caller
                        result.sendResult(firebaseMusicSource.asMediaItems())
                        // Prepare exoplayer when MediaItems MutableList ready
                        if(!isPlayerInitialized && firebaseMusicSource.songsMetadata.isNotEmpty()){
                            prepareExoplayer(firebaseMusicSource.songsMetadata, firebaseMusicSource.songsMetadata[0], false)
                            isPlayerInitialized = true
                        }
                    } else {
                        // Send Event when null returned
                        mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                        result.sendResult(null)
                    }
                }
                if(!resultSent) {
                    // Detach this message from the current thread and allow the sendResult call to happen later.
                    result.detach()
                }
            }
        }
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

    // This class is used to propagate information (metadata) about song to notification
    private inner class MusicQueueNavigator(): TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
        return  firebaseMusicSource.songsMetadata[windowIndex].description
        }

    }
}


