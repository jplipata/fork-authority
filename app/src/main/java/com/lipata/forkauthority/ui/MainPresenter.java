package com.lipata.forkauthority.ui;

import android.location.Address;
import android.location.Location;
import android.util.Log;

import com.lipata.forkauthority.api.GeocoderApi;
import com.lipata.forkauthority.api.GooglePlayApi;
import com.lipata.forkauthority.api.yelp.YelpApi;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements Presenter {
    private final static String LOG_TAG = MainPresenter.class.getSimpleName();

    private MainView view;
    private GooglePlayApi googlePlayApi;
    private GeocoderApi geocoderApi;


    @Inject public MainPresenter(final GooglePlayApi googlePlayApi, final GeocoderApi geocoderApi) {
        this.googlePlayApi = googlePlayApi;
        this.geocoderApi = geocoderApi;
    }

    public void onError(String message){
        view.stopRefreshAnimation();
        view.showSnackBarIndefinite(message);
    }

    public void setLocationText(String text) {
        view.setLocationText(text);
    }

    public MainView getView() {
        return view;
    }

    public void setView(MainView view) {
        this.view = view;
    }

    @Override
    public void onBestLocation(Location location) {
        geocoderApi
                .getAddressObservable(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAddressReceived, throwable -> {
                    Log.e(LOG_TAG, throwable.getMessage(), throwable);
                });
    }

    private void onAddressReceived(Address address) {
        Log.d(LOG_TAG, address.toString());
        view.setLocationText(address.getAddressLine(1));
    }

    public void onFetchBusinessList() {
        if (!googlePlayApi.isLocationStale()) {
            // If the location has already been recently updated, no need to update it, go straight to querying yelp
            googlePlayApi.checkNetworkPermissionAndCallYelpApi();
        } else {
            // Connect to GooglePlayApi, which will trigger onConnect() callback, i.e. execute sequence of events
            executeGooglePlayApiLocation();
        }

    }

    public void executeGooglePlayApiLocation() {
        // Trigger UI progress bar, analytic
        view.onDeviceLocationRequested();

        // Request location from Google Play API
        if (!googlePlayApi.getClient().isConnected()) {
            googlePlayApi.getClient().connect();
        } else {
            googlePlayApi.checkDeviceLocationEnabled();
        }
    }

    public void callYelpApi(String searchTerm, String ll, String s) {
        new YelpApi(view).callYelpApi(searchTerm, ll, s);
    }
}
