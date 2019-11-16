package com.lipata.forkauthority.businesslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lipata.forkauthority.api.GeocoderApi
import com.lipata.forkauthority.api.GooglePlayApi
import com.lipata.forkauthority.data.ListComposer
import com.lipata.forkauthority.data.ListFetcher
import com.lipata.forkauthority.util.AddressParser
import javax.inject.Inject

class BusinessListViewModelFactory @Inject constructor(
    private val listFetcher: ListFetcher,
    private val googlePlayApi: GooglePlayApi,
    private val geocoderApi: GeocoderApi,
    private val listComposer: ListComposer,
    private val addressParser: AddressParser
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            ListFetcher::class.java,
            GooglePlayApi::class.java,
            GeocoderApi::class.java,
            ListComposer::class.java,
            AddressParser::class.java
        ).newInstance(listFetcher, googlePlayApi, geocoderApi, listComposer, addressParser)
    }
}