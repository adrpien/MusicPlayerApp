package com.adrpien.musicplayerapp.data.entities

import android.net.Uri

data class Song(
    val id: String,
    val songURL: String,
    val coverURL: String,
    val title: String,
    val artist: String,
    val album: String,
    val subtitle: String,
    ){
}
