package com.adrpien.musicplayerapp.exoplayer



class FirebaseMusicSource {

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var songState: SongState = com.adrpien.musicplayerapp.exoplayer.SongState.STATE_CREATED
        set(value) {
            // call all lambdas from the list when songState is STATE_ERROR or STATE_INITIALIZED in thread save way
            if(value == com.adrpien.musicplayerapp.exoplayer.SongState.STATE_INITIALIZING ||  value == com.adrpien.musicplayerapp.exoplayer.SongState.STATE_ERROR) {

                // call all lambdas in a thread save way
                // What happens in synchronised block can be accesed only from its thread
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(songState == SongState.STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit ): Boolean {
        if (songState == SongState.STATE_CREATED || songState == SongState.STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(songState == SongState.STATE_INITIALIZED)
            return true
        }
    }
}

enum class SongState {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}