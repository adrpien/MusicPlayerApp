package com.adrpien.musicplayerapp.other



// Event class prevent specific events happen multiple times f.e. SnackBars
// When we rotate our device, livedata will emit again
open class Event<out T> (private val data: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if(hasBeenHandled){
            null
        } else {
            hasBeenHandled = true
            data
        }
    }

    // Return content even if has been handled
    fun peekContent() = data
}