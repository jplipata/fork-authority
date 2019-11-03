package com.lipata.forkauthority.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lipata.forkauthority.R;
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
    @ApplicationScope
    public Context provideContext() {
        return application;
    }

    @Provides
    @ApplicationScope
    SharedPreferences provideSharedPrefs(){
        return application.getSharedPreferences(application.getString(R.string.shared_prefs_file),
                Context.MODE_PRIVATE);
    }

    @Provides
    @ApplicationScope
    Application providesApplication(){
        return application;
    }

    @Provides
    @ApplicationScope
    AddressParser providesAddressParser(){
        return new AddressParser();
    }

}