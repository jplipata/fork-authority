package com.lipata.whatsforlunch.api;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.lipata.whatsforlunch.MainActivity;
import com.lipata.whatsforlunch.data.AppSettings;

/**
 * Created by jlipata on 4/2/16.
 */
public class DeviceLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOG_TAG = DeviceLocation.class.getSimpleName();

    final int LOCATION_REQUEST_INTERVAL = 1000; // in milliseconds
    final int LOCATION_REQUEST_FASTEST_INTERVAL = 1000;// in milliseconds
    final int MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID = 0;

    private MainActivity mMainActivity;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;
    long mLocationUpdateTimestamp; // in milliseconds

    public DeviceLocation(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
    }

    // Public methods

    // TODO Should this just go in the constructor?
    public void initialize(){
        mGoogleApiClient = new GoogleApiClient.Builder(mMainActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(LocationServices.API).build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
    }

    // TODO This method is really more of a UI method.  Should probably live somewhere else
    public void showLocation(){
        Log.d(LOG_TAG, "showLocation()...");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLocationUpdateTimestamp = SystemClock.elapsedRealtime();
        Log.d(LOG_TAG, "mLocationUpdateTimestamp = " + mLocationUpdateTimestamp);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            float accuracy = mLastLocation.getAccuracy();
            Log.d(LOG_TAG, "Success " + latitude + ", " + longitude + ", " + accuracy);

            mMainActivity.updateLocationViews(latitude, longitude, accuracy);

            stopLocationUpdates();
        } else {
            Log.d(LOG_TAG, "mLastLocation = null");
        }
    }

    public boolean isLocationStale(){
        long currentTime = SystemClock.elapsedRealtime();
        Log.d(LOG_TAG, "currentTime = " + currentTime);
        Log.d(LOG_TAG, "mLocationUpdateTimestamp = " + mLocationUpdateTimestamp);

        if ((currentTime - mLocationUpdateTimestamp) > AppSettings.LOCATION_LIFESPAN){
            return true;
        } else {
            return false;}
    }

    public void requestLocationData() {

        Log.d(LOG_TAG, "Creating LocationRequest...");

        // Check for Location permission
        boolean isPermissionMissing = ContextCompat.checkSelfPermission(mMainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        Log.d(LOG_TAG, "isPermissionMissing = " + isPermissionMissing);

        if(isPermissionMissing) {
            // If permission is missing, we need to ask for it.  See onRequestPermissionResult() callback
            ActivityCompat.requestPermissions((Activity) mMainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID);
        } else {

            // Else, permission has already been granted.  Proceed with requestLocationUpdates...
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                mGoogleApiClient.connect();
            }
        }
    }

    public void requestLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        // For our purposes, once we have the location, we no longer need the client, so disconnect
        mGoogleApiClient.disconnect();

        Log.d(LOG_TAG, "Location updates stopped and client disconnected");
    }

    // Callbacks for Google Play API
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG, "onConnected()");

        //TODO Implement presenter so that we're not talking directly to the UI
        mMainActivity.executeSequence();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        // https://developers.google.com/android/reference/com/google/android/gms/common/ConnectionResult

        int errorCode = result.getErrorCode();

        Log.i(LOG_TAG, "GoogleApiClient Connection failed: ConnectionResult.getErrorCode() = " + errorCode);

        // TODO The error code should be passed to the Presenter class (once it's written)
        mMainActivity.onGoogleApiConnectionFailed(errorCode);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(LOG_TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    // Callback method for LocationRequest
    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "Location Changed");
        showLocation();
    }

    // Getters

    public GoogleApiClient getClient(){
        return mGoogleApiClient;
    }

    public Location getLastLocation(){
        return mLastLocation;
    }

    public LocationRequest getLocationRequest(){
        return mLocationRequest;
    }

    public long getLocationUpdateTimestamp(){
        return  mLocationUpdateTimestamp;
    }

    // Setters
    public void setLocationUpdateTimestamp(long timestamp){
        mLocationUpdateTimestamp = timestamp;
    }

    // TODO Implement this
//    void isEnabledOnDevice(){
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//    }



}
