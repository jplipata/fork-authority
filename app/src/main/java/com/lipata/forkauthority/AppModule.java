package com.lipata.forkauthority;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lipata.forkauthority.di.PerApp;
import com.lipata.forkauthority.util.AddressParser;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @PerApp
    public Context provideContext() {
        return application;
    }

    @Provides
    @PerApp
    SharedPreferences provideSharedPrefs(){
        return application.getSharedPreferences(application.getString(R.string.shared_prefs_file),
                Context.MODE_PRIVATE);
    }

    @Provides
    @PerApp
    Application providesApplication(){
        return application;
    }

    @Provides
    @PerApp
    AddressParser providesAddressParser(){
        return new AddressParser();
    }

}