package com.lipata.forkauthority.ui;

import android.location.Address;
import android.location.Location;
import android.util.Log;

import com.lipata.forkauthority.util.Utility;
import com.lipata.forkauthority.api.GeocoderApi;
import com.lipata.forkauthority.api.GooglePlayApi;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.data.AppSettings;
import com.lipata.forkauthority.data.ListRanker;
import com.lipata.forkauthority.data.ListFetcher;

import java.util.List;

import javax.inject.Inject;

public class MainPresenter implements Presenter {
    private final static String LOG_TAG = MainPresenter.class.getSimpleName();

    private MainView view;
    private long callYelpApiStartTime;

    private final ListFetcher fetcher;
    private final GooglePlayApi googlePlayApi;
    private final GeocoderApi geocoderApi;
    private final ListRanker listRanker;

    @Inject
    MainPresenter(
            final ListFetcher fetcher,
            final GooglePlayApi googlePlayApi,
            final GeocoderApi geocoderApi,
            final ListRanker listRanker) {
        this.fetcher = fetcher;
        this.googlePlayApi = googlePlayApi;
        this.geocoderApi = geocoderApi;
        this.listRanker = listRanker;
    }

    @Override
    public void onBestLocation(Location location) {
        geocoderApi
                .getAddressObservable(location)
                .compose(Utility::applySchedulers)
                .subscribe(this::onAddressReceived, throwable -> {
                    Log.e(LOG_TAG, throwable.getMessage(), throwable);
                });
    }

    public void checkNetworkPermissionAndCallYelpApi(Location location) {
        // If connected to network make Yelp API call, if no network, notify user
        if (view.isNetworkConnected()) {
            Log.d(LOG_TAG, "Querying YelpV3api... Search term: " + AppSettings.SEARCH_TERM + " | Location: " + location.toString());
            callYelpApiStartTime = System.nanoTime();
            //get list
            fetcher
                    .getList(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()))
                    .subscribe(this::onListReceived, this::onError);

        } else {
            view.showSnackBarIndefinite("No network. Try again when you are connected to the internet.");
            view.stopRefreshAnimation();
        }
    }

    public void setView(MainView view) {
        this.view = view;
    }

    void onFetchBusinessList() {
        if (!googlePlayApi.isLocationStale()) {
            // If the location has already been recently updated, no need to update it, go straight to querying yelp
            checkNetworkPermissionAndCallYelpApi(googlePlayApi.getLastLocation());
        } else {
            // Connect to GooglePlayApi, which will trigger onConnect() callback, i.e. execute sequence of events
            executeGooglePlayApiLocation();
        }

    }

    void executeGooglePlayApiLocation() {
        // Trigger UI progress bar, analytic
        view.onDeviceLocationRequested();

        // Request location from Google Play API
        if (!googlePlayApi.getClient().isConnected()) {
            googlePlayApi.getClient().connect();
        } else {
            googlePlayApi.checkDeviceLocationEnabled();
        }
    }

    void onError(Throwable e) {
        view.stopRefreshAnimation();
        view.showSnackBarIndefinite(e.getMessage());
        Log.e(LOG_TAG, e.getMessage(), e);
    }

    private void onAddressReceived(Address address) {
        Log.d(LOG_TAG, "onAddressReceived() " + address.toString());

        // Check for parsed Address for completeness, if it's missing fields, don't update LocationText
        // LocationText should previously display latlong
        String text = Utility.parseLocAddress(address);
        if (text != null) {
            view.setLocationText(text);
        }
    }

    private void onListReceived(List<Business> businesses) {
        Log.d(LOG_TAG, "Total results received " + businesses.size());

        // Pass list to ListRanker to be processed
        List<Business> filteredBusinesses = listRanker.filter(businesses);

        // Update UI
        BusinessListAdapter businessListAdapter = view.getSuggestionListAdapter();
        businessListAdapter.setBusinessList(filteredBusinesses);
        businessListAdapter.notifyDataSetChanged();

        // Analytics
        Utility.reportExecutionTime(this, "callYelpApi sequence, time to get " + businesses.size() + " businesses", callYelpApiStartTime);
        view.logFabricAnswersMetric(AppSettings.FABRIC_METRIC_YELPAPI, callYelpApiStartTime);

        // UI
        view.onNewBusinessListReceived();
        view.stopRefreshAnimation();
    }
}
