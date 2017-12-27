package com.lipata.forkauthority;

import android.app.Application;

import com.lipata.forkauthority.api.yelp3.YelpModule;

import timber.log.Timber;

public class ForkAuthorityApp extends Application {
    private AppComponent appComponent;

    protected AppComponent initDagger(ForkAuthorityApp application) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .yelpModule(new YelpModule())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = initDagger(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
