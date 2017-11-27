package com.lipata.forkauthority.api;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.lipata.forkauthority.data.AppSettings;
import com.lipata.forkauthority.di.PerApp;
import com.lipata.forkauthority.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.lipata.forkauthority.ui.LocationQualityView.Status.BAD;
import static com.lipata.forkauthority.ui.LocationQualityView.Status.BEST;
import static com.lipata.forkauthority.ui.LocationQualityView.Status.OK;

/**
 * Created by jlipata on 4/2/16.
 *
 * Class responsible for obtaining device location.
 *
 * Because this class requires a substantial amount of calls to the Activity due to permissions,
 * location services, etc., we bypass the presenter and reference the Activity directly.
 *
 * I almost wonder if it would be better to just include this as part of the activity class...
 */
@PerApp
public class GooglePlayApi implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {
    private static final String LOG_TAG = GooglePlayApi.class.getSimpleName();

    public static final float LOCATION_QUALITY_THRESHOLD_BEST = 100f;
    public static final float LOCATION_QUALITY_THRESHOLD_BAD = 499f;

    // Google Play API - Location Setting Request.  Constant used in the location settings dialog.
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private final int LOCATION_REQUEST_INTERVAL = 20; // in milliseconds
    private final int LOCATION_REQUEST_FASTEST_INTERVAL = 20;// in milliseconds

    private final int ACCURACY_TOLERANCE = 200; // meters

    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID = 0;

    /**
     * Number of Location objects to receive before evaluating and returning the best one
     * Originally set this at 3 but it would take up to 15 seconds to execute on S3 Kitkat
     */
    private final int LOCATION_REQUEST_SAMPLE_SIZE = 2;

    private MainActivity mMainActivity;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private long mLocationUpdateTimestamp; // in milliseconds

    private List<Location> mLocationArray;

    private long mLastLocationChangeTime;

    @Inject GooglePlayApi(final Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        /**
         * Regarding these settings see:
         * https://docs.google.com/spreadsheets/d/1_4iC9dEOrl-cU8FEk7F7xt_BzUfaCebd6lrSfJe2BUE/edit?usp=sharing
         * https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#setMaxWaitTime(long)
         * http://stackoverflow.com/questions/16713659/locationrequest-in-google-play-services-isnt-updating-for-interval-set-as-less
         */
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL)
        //.setMaxWaitTime(LOCATION_REQUEST_MAX_WAIT_TIME)
        ;
    }

    // Callbacks for Google Play API

    /**
     * This is the first step/entry point in the sequence of execution steps, unless the client is already connected
     * See MainActivity.executeGooglePlayApiLocation()
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG, "onConnected()");

        @SuppressLint("MissingPermission")
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null && location.getAccuracy() < ACCURACY_TOLERANCE) {
            Log.d(LOG_TAG, "LastLocation not null and within ACCURACY_TOLERANCE");

            onBestLocationDetermined(location);
        } else {
            checkDeviceLocationEnabled();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        // https://developers.google.com/android/reference/com/google/android/gms/common/ConnectionResult

        int errorCode = result.getErrorCode();

        Log.i(LOG_TAG, "GoogleApiClient Connection failed: ConnectionResult.getErrorCode() = " + errorCode);

        switch (errorCode) {
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
        Log.d(LOG_TAG, "onLocationChanged() Execution analytics: Time since last update " + ((System.nanoTime() - mLastLocationChangeTime) / 1000000) + " ms");
        mLastLocationChangeTime = System.nanoTime();
        mLocationArray.add(location);
        areAllLocationsReceived();
    }

    // Callback for LocationSettingsRequest
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
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

    public void checkDeviceLocationEnabled() {

        Log.d(LOG_TAG, "Checking that Location is enabled on device...");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(this);

        // See onResult() callback for next steps...

    }

    public boolean isLocationStale() {
        long currentTime = SystemClock.elapsedRealtime();
        Log.d(LOG_TAG, "currentTime = " + currentTime);
        Log.d(LOG_TAG, "mLocationUpdateTimestamp = " + mLocationUpdateTimestamp);

        if (getLastLocation() == null) {
            return true;
        } else if ((currentTime - mLocationUpdateTimestamp) > AppSettings.LOCATION_LIFESPAN) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Public method called by onRequestPermissionsResult in MainActivity
     */
    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        // We want to get a few locations from the API and pick the best one
        // We'll store them in an array
        mLocationArray = new ArrayList<>();

        // Timestamp for individual location updates { onLocationChanged() }
        mLastLocationChangeTime = System.nanoTime();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {

        if (mGoogleApiClient.isConnected()) {
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
    private void areAllLocationsReceived() {
        Log.d(LOG_TAG, String.format("Location request #%d", mLocationArray.size()));
        if (mLocationArray.size() >= LOCATION_REQUEST_SAMPLE_SIZE) {
            stopLocationUpdates();
            onBestLocationDetermined(getBestLocation());
        }
    }

    private Location getBestLocation() {
        Location bestLocation = mLocationArray.get(0); // Get the first one, we'll compare next
        Log.d(LOG_TAG, String.format("Location #0: Accuracy %f", mLocationArray.get(0).getAccuracy()));

        for (int i = 1 /* Start with the second one */; i < mLocationArray.size(); i++) {
            float accuracy = mLocationArray.get(i).getAccuracy();
            Log.d(LOG_TAG, String.format("Location #%d: Accuracy %f", i, accuracy));
            if (accuracy < bestLocation.getAccuracy()) {
                bestLocation = mLocationArray.get(i);
            }
        }
        Log.d(LOG_TAG, String.format("Best location: Accuracy %f", bestLocation.getAccuracy()));
        return bestLocation;
    }

    private void onBestLocationDetermined(Location location) {
        updateLastLocationAndUpdateUI(location);
        mMainActivity.getPresenter().onBestLocation(location);
        mMainActivity.getPresenter().checkNetworkPermissionAndCallYelpApi(location);
    }

    private void checkLocationPermissionAndRequestLocation() {
        // Check for Location permission
        boolean isPermissionMissing = ContextCompat.checkSelfPermission(mMainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        Log.d(LOG_TAG, "isPermissionMissing = " + isPermissionMissing);

        if (isPermissionMissing) {
            // If permission is missing, we need to ask for it.  See onRequestPermissionResult() callback
            ActivityCompat.requestPermissions(mMainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID);
        } else {

            // Else, permission has already been granted.  Proceed with requestLocationUpdates...
            if (mGoogleApiClient.isConnected()) {
                Log.d(LOG_TAG, "Google API is connected.  Requesting Location Updates...");
                requestLocationUpdates();
            } else {
                Log.d(LOG_TAG, "Google API not connected.  Reconnecting...");
                mGoogleApiClient.connect();
            }
        }
    }

    private void updateLastLocationAndUpdateUI(Location location) {
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

            mMainActivity.updateLocationViews(latitude, longitude, getLocationQuality(accuracy));
            mMainActivity.onDeviceLocationRetrieved();
        } else {
            Log.d(LOG_TAG, "mLastLocation = null");
        }
    }

    private int getLocationQuality(float accuracy) {
        Log.d(LOG_TAG, "getLocationQuality() accuracy " + accuracy);
        if (accuracy < LOCATION_QUALITY_THRESHOLD_BEST) {
            return BEST;
        } else if (accuracy > LOCATION_QUALITY_THRESHOLD_BAD) {
            return BAD;
        } else return OK;
    }

    // Getters

    public GoogleApiClient getClient() {
        return mGoogleApiClient;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public long getLocationUpdateTimestamp() {
        return mLocationUpdateTimestamp;
    }

    // Setters
    public void setLocationUpdateTimestamp(long timestamp) {
        mLocationUpdateTimestamp = timestamp;
    }

    public void setActivity(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
    }
}
