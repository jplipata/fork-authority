package com.lipata.whatsforlunch.api;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.lipata.whatsforlunch.Utility;
import com.lipata.whatsforlunch.api.yelp.YelpApi;
import com.lipata.whatsforlunch.data.AppSettings;
import com.lipata.whatsforlunch.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by jlipata on 4/2/16.
 * Class responsible for obtaining device location.
 */
public class GooglePlayApi implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {

    private static final String LOG_TAG = GooglePlayApi.class.getSimpleName();

    final int LOCATION_REQUEST_INTERVAL = 20; // in milliseconds
    final int LOCATION_REQUEST_FASTEST_INTERVAL = 20;// in milliseconds

    final int MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID = 0;
    final int LOCATION_REQUEST_SAMPLE_SIZE = 3; // Number of Location objects to receive before evaluating and returning the best one

    // Google Play API - Location Setting Request.  Constant used in the location settings dialog.
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    // TODO Need to abstract this
    private MainActivity mMainActivity;

    private GeocoderApi mGeocoder;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;
    long mLocationUpdateTimestamp; // in milliseconds

    private List<Location> mLocationArray;

    private long mRequestLocationStartTime;
    private long mLastLocationChangeTime;

    public GooglePlayApi(MainActivity mainActivity, GeocoderApi geocoder) {
        this.mMainActivity = mainActivity;
        this.mGeocoder = geocoder;

        mGoogleApiClient = new GoogleApiClient.Builder(mMainActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                 .addApi(LocationServices.API).build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL)
                //.setMaxWaitTime(LOCATION_REQUEST_MAX_WAIT_TIME)
            ;
    }

    // Callbacks for Google Play API
    @Override
    public void onConnected(Bundle connectionHint) {

        /*
         * This is the first step/entry point in the sequence of execution steps
         */

        Log.d(LOG_TAG, "onConnected()");

        checkDeviceLocationEnabled();

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        // https://developers.google.com/android/reference/com/google/android/gms/common/ConnectionResult

        int errorCode = result.getErrorCode();

        Log.i(LOG_TAG, "GoogleApiClient Connection failed: ConnectionResult.getErrorCode() = " + errorCode);

        switch (errorCode){
            case 1:
                mMainActivity.showSnackBarIndefinite("ERROR: Google Play services is missing on this device");
                break;
            case 2:
                mMainActivity.showSnackBarIndefinite("ERROR: The installed version of Google Play services is out of date.");
                break;
            default:
                mMainActivity.showSnackBarIndefinite("ERROR: Google API Client, error code: " + errorCode);
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(LOG_TAG, "Connection suspended");
        //mGoogleApiClient.connect();
        mMainActivity.showSnackBarIndefinite("GooglePlayApi connection suspended.");
    }

    // Callback method for LocationRequest
    @Override
    public void onLocationChanged(Location location) {

        Log.d(LOG_TAG,"onLocationChanged() Execution analytics: Time since last update "+ ((System.nanoTime()- mLastLocationChangeTime)/1000000)+" ms");

        mLastLocationChangeTime = System.nanoTime();

        mLocationArray.add(location);

        isBestLocation();
    }

    // Callback for LocationSettingsRequest
    @Override public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();

        Log.d(LOG_TAG, "Location Settings result received. Code = " + status.getStatusCode());

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(LOG_TAG, "SUCCESS All location settings are satisfied.  Checking Location Permission and requesting location...");

                checkLocationPermissionAndRequestLocation();

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(LOG_TAG, "RESOLUTION_REQUIRED Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(mMainActivity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(LOG_TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(LOG_TAG, "SETTINGS_CHANGE_UNAVAILABLE Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                mMainActivity.stopRefreshAnimation();
                mMainActivity.showSnackBarIndefinite("There was an error.  Please check your settings.");
                break;
        }
    }

    // Public methods

    public void checkDeviceLocationEnabled(){

        Log.d(LOG_TAG, "Checking that Location is enabled on device...");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(this);

        // See onResult() callback for next steps...

    }

    public boolean isLocationStale(){
        long currentTime = SystemClock.elapsedRealtime();
        Log.d(LOG_TAG, "currentTime = " + currentTime);
        Log.d(LOG_TAG, "mLocationUpdateTimestamp = " + mLocationUpdateTimestamp);

        if(getLastLocation()==null){
            return true;
        } else if ((currentTime - mLocationUpdateTimestamp) > AppSettings.LOCATION_LIFESPAN){
            return true;
        } else {
            return false;}
    }

    public void requestLocationUpdates(){

        /*
         * Public method called by onRequestPermissionsResult in MainActivity
         */

        mMainActivity.showToast("Getting your location...");

        // Timestamp to measure entire location request process
        mRequestLocationStartTime = System.nanoTime();

        // We want to get a few locations from the API and pick the best one
        // We'll store them in an array
        mLocationArray = new ArrayList<>();

        // Timestamp for individual location updates { onLocationChanged() }
        mLastLocationChangeTime = System.nanoTime();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {

        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            // For our purposes, once we have the location, we no longer need the client, so disconnect
            mGoogleApiClient.disconnect();

            Log.d(LOG_TAG, "Location updates stopped and client disconnected");
        } else {
            Log.d(LOG_TAG, "stopLocationUpdates() Google Api Client already disconnected");
        }
    }

    // Helper methods

    /***
     * This method is called every time the Google Play Api returns a new Location to the `onLocationChanged` callback
     * It determines whether the number of retries have been met.  If yes, it stops further location requests
     * and triggers to proceed with the best location.  If no, it does nothing and waits for the next location
     */
    private void isBestLocation() {
        Log.d(LOG_TAG, String.format("Location request #%d", mLocationArray.size()));
        if(mLocationArray.size()>= LOCATION_REQUEST_SAMPLE_SIZE){
            stopLocationUpdates();
            onBestLocationDetermined(identifyBestLocation());
        }
    }

    private Location identifyBestLocation() {

        Location bestLocation = mLocationArray.get(0); // Get the first one, we'll compare next
        Log.d(LOG_TAG, String.format("Location #0: Accuracy %f", mLocationArray.get(0).getAccuracy()));

        for(int i=1 /* Start with the second one */ ; i<mLocationArray.size(); i++){
            float accuracy = mLocationArray.get(i).getAccuracy();
            Log.d(LOG_TAG, String.format("Location #%d: Accuracy %f", i, accuracy));
            if(accuracy<bestLocation.getAccuracy()){
                bestLocation = mLocationArray.get(i);
            }
        }
        Log.d(LOG_TAG, String.format("Best location: Accuracy %f", bestLocation.getAccuracy()));
        return bestLocation;
    }

    private void onBestLocationDetermined(Location location) {
        updateLastLocationAndUpdateUI(location);

        // Call Geocoder via RxJava
        // TODO Consider moving this Subscriber to the UI, e.g. MainActivity
        mGeocoder.getAddressObservable(location).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Address>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        //mMainActivity.setLocationText("Lookup error");

                        // If there's an error with reverse geo lookup, we'll just show the lat,long coordinates instead
                    }

                    @Override
                    public void onNext(Address address) {

                        Log.d(LOG_TAG, address.toString());
                        mMainActivity.setLocationText(address.getAddressLine(0)+", "+address.getAddressLine(1));

                    }
                });

        checkNetworkPermissionAndCallYelpApi();

        Utility.reportExecutionAnalytics(this, "Google Play Api get location", mRequestLocationStartTime);
    }

    private void checkLocationPermissionAndRequestLocation() {

        // Check for Location permission
        boolean isPermissionMissing = ContextCompat.checkSelfPermission(mMainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        Log.d(LOG_TAG, "isPermissionMissing = " + isPermissionMissing);

        if(isPermissionMissing) {
            // If permission is missing, we need to ask for it.  See onRequestPermissionResult() callback
            ActivityCompat.requestPermissions(mMainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID);
        } else {

            // Else, permission has already been granted.  Proceed with requestLocationUpdates...
            if(mGoogleApiClient.isConnected()) {
                Log.d(LOG_TAG, "Google API is connected.  Requesting Location Updates...");
                requestLocationUpdates();
            } else {
                Log.d(LOG_TAG, "Google API not connected.  Reconnecting...");
                mGoogleApiClient.connect();
            }
        }
    }

    private void updateLastLocationAndUpdateUI(Location location){
        Log.d(LOG_TAG, "updateLastLocationAndUpdateUI()...");

        // Get last location & update timestamp
        mLastLocation = location;
        mLocationUpdateTimestamp = SystemClock.elapsedRealtime();
        Log.d(LOG_TAG, "mLocationUpdateTimestamp = " + mLocationUpdateTimestamp);

        // If LastLocation is not null, pass to MainActivity to be displayed
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            float accuracy = mLastLocation.getAccuracy();
            Log.d(LOG_TAG, "Success " + latitude + ", " + longitude + ", " + accuracy);

            mMainActivity.updateLocationViews(latitude, longitude, accuracy);
        } else {
            Log.d(LOG_TAG, "mLastLocation = null");
        }
    }

    public void checkNetworkPermissionAndCallYelpApi(){
        // Check for network connectivity
        ConnectivityManager cm = (ConnectivityManager)mMainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If connected to network make Yelp API call, if no network, notify user
        if(isConnected) {
            String ll = getLastLocation().getLatitude() + ","
                    + getLastLocation().getLongitude() + ","
                    + getLastLocation().getAccuracy();
            Log.d(LOG_TAG, "Querying Yelp... ll = " + ll + " Search term: " + AppSettings.SEARCH_TERM);
            new YelpApi(mMainActivity).callYelpApi(AppSettings.SEARCH_TERM, ll, Integer.toString(AppSettings.SEARCH_RADIUS));
        } else {

            // UI
            mMainActivity.showSnackBarIndefinite("No network. Try again when you are connected to the internet.");
            mMainActivity.stopRefreshAnimation();
        }

    }

    // Getters

    public GoogleApiClient getClient(){
        return mGoogleApiClient;
    }

    public Location getLastLocation(){
        return mLastLocation;
    }

    public long getLocationUpdateTimestamp(){
        return  mLocationUpdateTimestamp;
    }

    // Setters
    public void setLocationUpdateTimestamp(long timestamp){
        mLocationUpdateTimestamp = timestamp;
    }

}
