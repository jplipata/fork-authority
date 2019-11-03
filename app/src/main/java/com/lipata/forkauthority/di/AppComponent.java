package com.lipata.forkauthority.di;

import com.lipata.forkauthority.api.yelp3.YelpModule;
import com.lipata.forkauthority.ui.RestaurantListActivity;

import dagger.Component;

@ApplicationScope
@Component(modules = {AppModule.class, YelpModule.class})
public interface AppComponent {
    void inject(RestaurantListActivity target);
}