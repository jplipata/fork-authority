package com.lipata.forkauthority.businesslist;

import android.location.Address;
import android.location.Location;

import com.lipata.forkauthority.api.GeocoderApi;
import com.lipata.forkauthority.api.GooglePlayApi;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.data.AppSettings;
import com.lipata.forkauthority.data.ListFetcher;
import com.lipata.forkauthority.data.ListComposer;
import com.lipata.forkauthority.data.CombinedList;
import com.lipata.forkauthority.util.AddressParser;
import com.lipata.forkauthority.util.Utility;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

// TODO Dispose subscriptions!!
public class BusinessListPresenter extends ViewModel implements Presenter {

    private MainView view;
    private long callYelpApiStartTime;

    private final ListFetcher fetcher;
    private final GooglePlayApi googlePlayApi;
    private final GeocoderApi geocoderApi;
    private final ListComposer listComposer;
    private final AddressParser addressParser;

    private MutableLiveData<CombinedList> combinedListLiveData;

    @Inject public BusinessListPresenter(
            final ListFetcher fetcher,
            final GooglePlayApi googlePlayApi,
            final GeocoderApi geocoderApi,
            final ListComposer listComposer,
            final AddressParser addressParser) {
        this.fetcher = fetcher;
        this.googlePlayApi = googlePlayApi;
        this.geocoderApi = geocoderApi;
        this.listComposer = listComposer;
        this.addressParser = addressParser;
        this.combinedListLiveData = new MutableLiveData<>();
    }

    @Override
    public void onBestLocation(Location location) {
        geocoderApi
                .getAddressObservable(location)
                .compose(Utility::applySchedulers)
                .subscribe(this::onAddressReceived, throwable -> {
                    Timber.e(throwable.getMessage(), throwable);
                });
    }

    public void checkNetworkPermissionAndCallYelpApi(Location location) {
        // If connected to network make Yelp API call, if no network, notify user
        if (view.isNetworkConnected()) {
            Timber.d("Querying YelpV3api... Search term: " + AppSettings.SEARCH_TERM + " | Location: " + location.toString());
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
        Timber.e(e.getMessage(), e);
    }

    private void onAddressReceived(Address address) {
        Timber.d("onAddressReceived() " + address.toString());

        String text = addressParser.getFormattedAddress(address);
        if (!text.isEmpty()) {
            view.setLocationText(text);
        }
    }

    private void onListReceived(List<Business> businesses) {
        Timber.d("Total results received %s", businesses.size());

        if (businesses.size() > 0) {
            // Pass list to ListComposer to be processed
            CombinedList filteredBusinesses = listComposer.filter(businesses);

            // Update UI
            BusinessListAdapter businessListAdapter = view.getSuggestionListAdapter();
            businessListAdapter.setBusinessList(filteredBusinesses);
            businessListAdapter.notifyDataSetChanged();

            combinedListLiveData.setValue(filteredBusinesses);
        } else {
            view.onNoResults();
        }

        // Analytics
        Utility.reportExecutionTime(this, "callYelpApi sequence, time to get "
                + businesses.size() + " businesses", callYelpApiStartTime);
        view.logFabricAnswersMetric(AppSettings.FABRIC_METRIC_YELPAPI, callYelpApiStartTime);

        // UI
        view.onNewBusinessListReceived();
        view.stopRefreshAnimation();
    }
}
