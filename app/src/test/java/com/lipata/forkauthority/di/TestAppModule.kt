package com.lipata.forkauthority.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.lipata.forkauthority.businesslist.ExpirationProvider
import com.lipata.forkauthority.data.user.UserIdentityManager
import com.lipata.forkauthority.poll.viewpoll.ViewPollViewModel
import com.lipata.forkauthority.util.AddressParser
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides

@Module
class TestAppModule {
    @Provides
    @ApplicationScope
    fun provideContext(): Context {
        return mock()
    }

    @Provides
    @ApplicationScope
    internal fun provideSharedPrefs(): SharedPreferences {
        return mock()
    }

    @Provides
    @ApplicationScope
    internal fun providesApplication(): Application {
        return mock()
    }

    @Provides
    @ApplicationScope
    internal fun providesAddressParser(): AddressParser {
        return mock()
    }

    @Provides
    @ApplicationScope
    fun provideUserIdentityManager(): UserIdentityManager {
        return mock()
    }

    @Provides
    @ApplicationScope
    fun providePollViewModel(): ViewPollViewModel {
        return mock()
    }

    @Provides
    @ApplicationScope
    fun provideJustAteHerePref(context: Context): ExpirationProvider {
        return mock()
    }
}