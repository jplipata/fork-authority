package com.lipata.forkauthority.businesslist;

import android.location.Address;
import android.location.Location;

import com.lipata.forkauthority.api.GeocoderApi;
import com.lipata.forkauthority.api.GooglePlayApi;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.data.AppSettings;
import com.lipata.forkauthority.data.CombinedList;
import com.lipata.forkauthority.data.ListComposer;
import com.lipata.forkauthority.data.ListFetcher;
import com.lipata.forkauthority.util.AddressParser;
import com.lipata.forkauthority.util.Utility;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class BusinessListViewModel extends ViewModel {

    private long callYelpApiStartTime;

    private final ListFetcher fetcher;
    private final GooglePlayApi googlePlayApi;
    private final GeocoderApi geocoderApi;
    private final ListComposer listComposer;
    private final AddressParser addressParser;

    private MutableLiveData<FetchListState> listLiveData;
    private MutableLiveData<LocationState> locationLiveData;

    private CompositeDisposable compositeDisposable;

    @Inject public BusinessListViewModel(
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
        this.listLiveData = new MutableLiveData<>();
        this.locationLiveData = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();
    }

    void onStart() {
        boolean hasList = listLiveData.getValue() instanceof FetchListState.Success;
        if (!hasList) {
            fetchBusinessList();
        }
    }

    public void onBestLocation(Location location) {
        compositeDisposable.add(
                geocoderApi
                        .getAddressObservable(location)
                        .compose(Utility::applySchedulers)
                        .subscribe(this::onAddressReceived,
                                throwable -> Timber.e(throwable.getMessage(), throwable))
        );
    }

    public void checkNetworkPermissionAndCallYelpApi(Location location) {
        Timber.d("Querying YelpV3api... Search term: " + AppSettings.SEARCH_TERM + " | Location: " + location.toString());
        callYelpApiStartTime = System.nanoTime();

        //get list
        compositeDisposable.add(
                fetcher
                        .getList(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()))
                        .subscribe(this::onListReceived, this::onError)
        );
    }

    void fetchBusinessList() {
        listLiveData.setValue(new FetchListState.Loading());

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
        locationLiveData.setValue(new LocationState.Loading());

        // Request location from Google Play API
        if (!googlePlayApi.getClient().isConnected()) {
            googlePlayApi.getClient().connect();
        } else {
            googlePlayApi.checkDeviceLocationEnabled();
        }
    }

    private void onError(Throwable e) {
        listLiveData.setValue(new FetchListState.Error(e));
        Timber.e(e.getMessage(), e);
    }

    private void onAddressReceived(Address address) {
        Timber.d("onAddressReceived() " + address.toString());

        String text = addressParser.getFormattedAddress(address);
        if (!text.isEmpty()) {
            locationLiveData.setValue(new LocationState.Success(text));
        }
    }

    private void onListReceived(List<Business> businesses) {
        Timber.d("Total results received %s", businesses.size());

        if (businesses.size() > 0) {
            // Pass list to ListComposer to be processed
            CombinedList filteredBusinesses = listComposer.filter(businesses);

            // Update UI
            listLiveData.setValue(new FetchListState.Success(filteredBusinesses));
        } else {
            listLiveData.setValue(new FetchListState.NoResults());
        }

        // Analytics
        Utility.reportExecutionTime(this, "callYelpApi sequence, time to get "
                + businesses.size() + " businesses", callYelpApiStartTime);

        // TODO Fabric has been deprecated
        //view.logFabricAnswersMetric(AppSettings.FABRIC_METRIC_YELPAPI, callYelpApiStartTime);
    }

    @Override protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    //region Getters/Setters
    MutableLiveData<FetchListState> getListLiveData() {
        return listLiveData;
    }

    MutableLiveData<LocationState> getLocationLiveData() {
        return locationLiveData;
    }
    //endregion

}
