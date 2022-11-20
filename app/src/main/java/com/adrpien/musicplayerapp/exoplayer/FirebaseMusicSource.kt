package com.adrpien.musicplayerapp.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.adrpien.musicplayerapp.data.remote.MusicDatabase
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
) {

    /*
     MediaMetadataCompat contains meta-information about the song
     MediaSource contain information where Exoplayer can stream song from
     MediaItem is single item in "file manager" of app" - can be browsable or playable
     */

    // List of lambdas to execute, when downloading is succesfully or unsuccesfully finished
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    // Song State
    // Setter set the field and execute each lambda iterating onReadyListeners, when state is error or initilized,
    // and set the field in other case
    private var songState: SongState = com.adrpien.musicplayerapp.exoplayer.SongState.STATE_CREATED
        set(value) {
            // call all lambdas from the list when songState is STATE_ERROR or STATE_INITIALIZED in thread save way
            if(value == com.adrpien.musicplayerapp.exoplayer.SongState.STATE_INITIALIZED ||  value == com.adrpien.musicplayerapp.exoplayer.SongState.STATE_ERROR) {

                // call all lambdas in a thread save way
                // What happens in synchronised block can be accesed only from its thread
                /*
                ************* SYNCHRONIZED METHODS ************
                * A thread that enters a synchronized method obtains a lock
                * (an object being locked is the instance of the containing class)
                * and no other thread can enter the method until the lock is released.
                 */
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(songState == SongState.STATE_INITIALIZED)
                    }
                }
            } else  {
                field = value
            }
        }

    // Songs metadata list initialization
    var songsMetadata = emptyList<MediaMetadataCompat>()

    // fun which actually fetch song objects from firebase
    // Switch currently running thread of the coroutine to IO Thread which is optimized for IO operations
    // which fits for database/network operations
    // Switch current coroutine context to Dispatchers.IO
    suspend fun fetchMetaData()  {
        songState = SongState.STATE_INITIALIZING
        getSongList()
        songState =  SongState.STATE_INITIALIZED
    }

    suspend fun getSongList() = withContext(Dispatchers.IO)  {
        val allSongs = musicDatabase.getSongList()
        songsMetadata = allSongs.map { song ->
            Builder()
                .putString(METADATA_KEY_ARTIST, song.artist)
                .putString(METADATA_KEY_MEDIA_ID, song.id)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(METADATA_KEY_TITLE, song.title)
                .putString(METADATA_KEY_MEDIA_URI, song.songURL)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.coverURL)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.coverURL)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.subtitle)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.subtitle)
                .build()
        }
    }


    // Adds lambda to onReadyListeners, when downloading is not fishied or even started
    // and call the lambda when downloading is finished succesfully or unsuccesfully
    fun whenReady(action: (Boolean) -> Unit ): Boolean {
        if (songState == SongState.STATE_CREATED || songState == SongState.STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(songState == SongState.STATE_INITIALIZED)
            return true
        }
    }

    fun asMediaItems() =
        songsMetadata.map { song ->
            val mediaDescriptionCompat = MediaDescriptionCompat.Builder()
                .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
                .setTitle(song.description.title)
                .setSubtitle(song.description.subtitle)
                .setIconUri(song.description.iconUri)
                .setMediaId(song.description.mediaId)
                .build()
            MediaBrowserCompat.MediaItem(mediaDescriptionCompat, FLAG_PLAYABLE)
        }.toMutableList()

    // Media source is single song which can by played by Exoplayer
    // Function creates concatinating single music sources (list of single music sourcess)
    // Converting s
    fun asMediaSource(dataSource: DefaultDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songsMetadata.forEach{ song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSource)
                .createMediaSource(MediaItem.fromUri(song.getString(METADATA_KEY_MEDIA_URI)))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

}

enum class SongState {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}