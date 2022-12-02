package com.adrpien.musicplayerapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrpien.musicplayerapp.exoplayer.MusicServiceConnection
import com.adrpien.musicplayerapp.exoplayer.PlayerMediaBrowserService
import com.adrpien.musicplayerapp.exoplayer.songCurrentMillisecond
import com.adrpien.musicplayerapp.other.Constants.UPDATE_SONG_CURRENT_MILLISECOND_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
): ViewModel() {

    private val playbackState = musicServiceConnection.playbackState

    private var _currentSongDuration = MutableLiveData<Long>()
    var currentSongDuration: LiveData<Long> = _currentSongDuration

    private var _songCurrentMillisecond = MutableLiveData<Long>()
    var songCurrentMillisecond: LiveData<Long> = _songCurrentMillisecond

    init {
        updateCurrentSongMillisecond()
    }

    private fun updateCurrentSongMillisecond() {
        viewModelScope.launch {
            while (true) {
                val songMillisecond = playbackState.value?.songCurrentMillisecond
                songMillisecond?.let {
                    if (songCurrentMillisecond.value != it) {
                        _songCurrentMillisecond.postValue(it)
                        _currentSongDuration.postValue(PlayerMediaBrowserService.songDuration)
                    }
                }
                delay(UPDATE_SONG_CURRENT_MILLISECOND_INTERVAL)
            }
        }
    }
}