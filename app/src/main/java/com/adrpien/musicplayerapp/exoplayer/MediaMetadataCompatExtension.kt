package com.adrpien.musicplayerapp.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.adrpien.musicplayerapp.data.entities.Song

fun MediaMetadataCompat.toSong():Song? {
    return description?.let {
        Song(
            it.mediaId ?: "",
            it.mediaUri.toString(),
            it.iconUri.toString(),
            it.title.toString(),
            it.subtitle.toString(),
            it.subtitle.toString(),
            it.subtitle.toString()
            )
    }

}