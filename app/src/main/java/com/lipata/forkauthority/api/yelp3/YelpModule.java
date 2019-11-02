package com.lipata.forkauthority.api.yelp3;

import android.app.Application;
import android.content.SharedPreferences;

import com.lipata.forkauthority.di.PerApp;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jlipata on 11/24/17.
 */

@Module
public class YelpModule {
    @Provides
    @PerApp
    Yelp3ApiClient providesYelp3ApiClient(Yelp3ApiAuthInterceptor authInterceptor) {
        return new Yelp3ApiClient(authInterceptor);
    }

    @Provides
    @PerApp
    Yelp3ApiAuthInterceptor providesYelp3ApiAuthInterceptor() {
        return new Yelp3ApiAuthInterceptor();
    }

    @Provides
    @PerApp
    TokenManager provideTokenManager(Application application, SharedPreferences sharedPrefs) {
        return new TokenManager(application, sharedPrefs);
    }
}
