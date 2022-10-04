package com.adrpien.musicplayerapp.data.remote

import com.adrpien.musicplayerapp.data.entities.Song
import com.adrpien.musicplayerapp.other.Constants.SONG_LIST
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MusicDatabase {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val songCollection = firebaseFirestore.collection(SONG_LIST)


    suspend fun getSongList(){
        try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            emptyList<Song>()
        }
    }
}