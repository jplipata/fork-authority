package com.lipata.forkauthority;

import com.lipata.forkauthority.api.yelp3.YelpModule;
import com.lipata.forkauthority.di.PerApp;
import com.lipata.forkauthority.ui.MainActivity;

import dagger.Component;

@PerApp
@Component(modules = {AppModule.class, YelpModule.class})
public interface AppComponent {
    void inject(MainActivity target);
}