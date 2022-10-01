package com.adrpien.musicplayerapp

import android.net.Uri

data class Song(val uri: Uri, val name: String, val artist: String, val album: String, val duration: String){
}
