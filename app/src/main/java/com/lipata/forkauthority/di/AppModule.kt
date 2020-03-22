package com.lipata.forkauthority.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.lipata.forkauthority.R
import com.lipata.forkauthority.businesslist.ExpirationProvider
import com.lipata.forkauthority.businesslist.JustAteHereExpirationProviderImpl
import com.lipata.forkauthority.util.AddressParser
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {
    @Provides
    @ApplicationScope
    fun provideContext(): Context {
        return application
    }

    @Provides
    @ApplicationScope
    fun provideSharedPrefs(): SharedPreferences {
        return application.getSharedPreferences(
            application.getString(R.string.shared_prefs_file),
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @ApplicationScope
    fun providesApplication(): Application {
        return application
    }

    @Provides
    @ApplicationScope
    fun providesAddressParser(): AddressParser {
        return AddressParser()
    }

    @Provides
    @ApplicationScope
    fun provideJustAteHerePref(context: Context): ExpirationProvider {
        return JustAteHereExpirationProviderImpl(context)
    }
}
