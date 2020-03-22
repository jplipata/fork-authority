package com.lipata.forkauthority.di

import com.lipata.forkauthority.api.yelp3.Yelp3ApiAuthInterceptor
import com.lipata.forkauthority.api.yelp3.Yelp3ApiClient
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides

@Module
class TestYelpModule {
    @Provides
    @ApplicationScope
    fun providesYelp3ApiClient(): Yelp3ApiClient {
        return mock()
    }

    @Provides
    @ApplicationScope
    fun providesYelp3ApiAuthInterceptor(): Yelp3ApiAuthInterceptor {
        return mock()
    }
}
