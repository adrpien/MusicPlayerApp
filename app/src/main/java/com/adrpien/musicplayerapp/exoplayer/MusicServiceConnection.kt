package com.adrpien.musicplayerapp.exoplayer

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import android.support.v4.media.session.MediaControllerCompat.getMediaController
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adrpien.musicplayerapp.other.Constants.NETWORK_ERROR
import com.adrpien.musicplayerapp.other.Event
import com.adrpien.musicplayerapp.other.Resource
import com.adrpien.musicplayerapp.other.ResourceState

// This class is used for exchanging information between activity or class and service
class MusicServiceConnection(
    context: Context,
) {

    /*
    Livedata object, which can be observed, contains current state of data.
    Data inside MutableLiveData  object can be changed only inside of the class,
    then ImmutableLiveData is exposed (public val) to another classes, where can be observed
    */

    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _currentlyPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentlyPlayingSong: LiveData<MediaMetadataCompat?> = _currentlyPlayingSong

    // This object can be used to get access to transport controls (pause/play/skip) and to register some callbacks
    lateinit var mediaControllerCompat: MediaControllerCompat

    // MediaBrowser is  m ainly use to get access to session token and to subscribe and unsubscribe from service
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowserCompat = MediaBrowserCompat(
        context,
        ComponentName(context, PlayerMediaBrowserService::class.java),
        mediaBrowserConnectionCallback,
        null
    ). apply {
        connect()
    }

    // This getter will be used only when we actually access to this transport controls
    val transportControls: TransportControls
        get() = mediaControllerCompat.transportControls


    // Function subscribe MediaBrowser
    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback){
        mediaBrowserCompat.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback){
        mediaBrowserCompat.unsubscribe(parentId, callback)
    }

    // Callback for Media Browser
    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ): MediaBrowserCompat.ConnectionCallback() {


        override fun onConnected() {
            // Here we can create MediaControllerCompat
            mediaControllerCompat = MediaControllerCompat(context, mediaBrowserCompat.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true)))
         }

        override fun onConnectionSuspended() {
            _isConnected.postValue((Event(Resource.error(
                "Connection suspended",
                false
            )
            )))
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(
                Event(Resource.error(
                "Could not connect Media Browser",
                false
            )
                )
            )
        }
    }

    // Callbacks implementation
    private inner class MediaControllerCallback: MediaControllerCompat.Callback(){

        // Whenever our PlaybackState change, MutableLivedata object will be updated
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentlyPlayingSong.postValue(metadata)
        }

        // Used to send custom events from our service to connection callback
        // We use it to notify network error
        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event){
                NETWORK_ERROR -> {
                    _networkError.postValue(Event(Resource.error(
                        "Network Error",
                        null)))
                }
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}