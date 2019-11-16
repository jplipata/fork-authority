package com.lipata.forkauthority.businesslist

sealed class LocationState {
    class Loading: LocationState()
    class Success(val location: String): LocationState()
}