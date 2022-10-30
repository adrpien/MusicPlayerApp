package com.adrpien.musicplayerapp.exoplayer.callbacks

import android.app.Service.STOP_FOREGROUND_DETACH
import android.widget.Toast
import com.adrpien.musicplayerapp.exoplayer.PlayerMediaBrowserService
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(
    private val musicService: PlayerMediaBrowserService
    ): Player.Listener
{
    @Deprecated ("Use onPlaybackStateChanged and onPlayWhenReadyChanged insted")
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        if (!playWhenReady)  musicService.stopForeground(STOP_FOREGROUND_DETACH)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Error, check internet connection", Toast.LENGTH_SHORT).show()
    }
}