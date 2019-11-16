package com.lipata.forkauthority.data

sealed class Lce {
    object Loading : Lce()
    class Error(val error: Throwable) : Lce()
    class Content<T>(val content: T) : Lce()
}