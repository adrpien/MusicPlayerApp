package com.adrpien.musicplayerapp.ui.viewmodels

import android.media.session.PlaybackState
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adrpien.musicplayerapp.data.entities.Song
import com.adrpien.musicplayerapp.exoplayer.MusicServiceConnection
import com.adrpien.musicplayerapp.exoplayer.isPlayEnabled
import com.adrpien.musicplayerapp.exoplayer.isPlaying
import com.adrpien.musicplayerapp.exoplayer.isPrepared
import com.adrpien.musicplayerapp.other.Constants.MEDIA_ROOT_ID
import com.adrpien.musicplayerapp.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
     private val musicServiceConnection: MusicServiceConnection
): ViewModel() {

     private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
     val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

     val isConnected = musicServiceConnection.isConnected
     val networkError = musicServiceConnection.networkError
     val currentlyPlayingSong = musicServiceConnection.currentlyPlayingSong
     val playbackState = musicServiceConnection.playbackState

     init {
         _mediaItems.postValue(Resource.loading(null))
          musicServiceConnection.subscribe(MEDIA_ROOT_ID, object: MediaBrowserCompat.SubscriptionCallback(){
               override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
               ) {
                    val songs: List<Song> = children.map {
                         Song(
                              it.mediaId!!,
                              it.description.mediaUri.toString(),
                              it.description.iconUri.toString(),
                              it.description.title.toString(),
                              it.description.subtitle.toString(),
                              it.description.subtitle.toString(),
                              it.description.subtitle.toString()
                         )
                    }
                    _mediaItems.postValue(Resource.success(songs))
               }
          })
     }

     // Functions to control player
     fun skipToNextSong() = musicServiceConnection.transportControls.skipToNext()
     fun skipToPreviousSong() = musicServiceConnection.transportControls.skipToPrevious()
     fun seekTo(position: Long) = musicServiceConnection.transportControls.seekTo(position)

     // Pause if playing, play if paused and play new, if passed song is different than currently playing song
     fun  playOrToggleSong(song: Song, toggle: Boolean = false) {
          val isPrepared = playbackState.value?.isPrepared ?: false
          if (isPrepared && song.id == currentlyPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)){
               playbackState.value?.let { playbackState ->
                    when {
                         playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                         playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                         else -> Unit
                    }
               }
          } else {
               musicServiceConnection.transportControls.playFromMediaId(song.id, null)
          }
     }

     override fun onCleared() {
          super.onCleared()
          musicServiceConnection.unsubscribe(
               MEDIA_ROOT_ID,
               object: MediaBrowserCompat.SubscriptionCallback(){})
     }



}