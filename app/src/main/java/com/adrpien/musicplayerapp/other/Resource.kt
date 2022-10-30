package com.adrpien.musicplayerapp.other


/*
    *************** OUT KEYWORD *******************
    * List<out T> in Kotlin is equivalent to List<? extends T> in Java.
    * List<in T> in Kotlin is equivalent to List<? super T> in Java
 */
data class Resource <out T>(
    val resourceState: ResourceState,
    val data: T?,
    val message: String?
    ) {
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