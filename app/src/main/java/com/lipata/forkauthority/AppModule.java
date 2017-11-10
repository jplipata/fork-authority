package com.lipata.forkauthority;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPrefs(){
        return application.getSharedPreferences(application.getString(R.string.shared_prefs_file),
                Context.MODE_PRIVATE);
    }

}