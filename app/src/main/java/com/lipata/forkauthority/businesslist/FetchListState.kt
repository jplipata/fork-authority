package com.lipata.forkauthority.businesslist

import com.lipata.forkauthority.data.CombinedList

sealed class FetchListState {
    class Loading : FetchListState()
    class Success(val list: CombinedList) : FetchListState()
    class Error(val throwable: Throwable): FetchListState()
    class NoResults : FetchListState()
}