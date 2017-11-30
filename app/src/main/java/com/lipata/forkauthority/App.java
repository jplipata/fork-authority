package com.lipata.forkauthority;

import android.app.Application;

import com.lipata.forkauthority.api.yelp3.YelpModule;

public class App extends Application {
    private AppComponent appComponent;

    protected AppComponent initDagger(App application) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .yelpModule(new YelpModule())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = initDagger(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
