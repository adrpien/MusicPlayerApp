package com.adrpien.musicplayerapp.other


/*
    *************** OUT KEYWORD *******************
    * List<out T> in Kotlin is equivalent to List<? extends T> in Java.
    * List<in T> in Kotlin is equivalent to List<? super T> in Java
 */

// We can wrap any data, in order to have access to this data from activities and fragments
// Class for error handling
// Example: Wrapped around list of songs
data class Resource <out T>(
    val resourceState: ResourceState,
    val data: T?,
    val message: String?
    ) {
    /*
    ********** COMPANION OBJECT *********************
    *
     */
    companion object {
        fun <T> success(data: T?) = Resource(ResourceState.SUCCESS, data, null)

        fun <T> error(message: String?, data: T?) = Resource(ResourceState.ERROR, data, message)

        fun <T> loading(data: T?) = Resource(ResourceState.LOADING, data, null)
    }

}

enum class ResourceState {
    ERROR,
    SUCCESS ,
    LOADING
}