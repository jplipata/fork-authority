package com.lipata.whatsforlunch;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.lipata.whatsforlunch.yelp_api.YelpAPI;
import com.lipata.whatsforlunch.yelp_apiresponsepojo.Business;
import com.lipata.whatsforlunch.yelp_apiresponsepojo.YelpResponse;

import java.util.List;

/**
 *  Quick and dirty test project that gets device location, queries the Yelp API, and uses
 *  GSON to parse and display the response.
 */

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    // Location vars
    GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;


    // Views
    protected TextView mTextView_Latitude;
    protected TextView mTextView_Longitude;
    protected TextView mTextView_Accuracy;
    protected TextView mTextView_Other;

    // Constants
    final String SEARCH_TERM = "restaurants";
    static final String LOG_TAG = MainActivity.class.getSimpleName();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextView_Latitude = (TextView) findViewById((R.id.latitude_text));
        mTextView_Longitude = (TextView) findViewById((R.id.longitude_text));
        mTextView_Accuracy = (TextView) findViewById(R.id.accuracy_text);
        mTextView_Other = (TextView) findViewById((R.id.otherlocationdata_text));

        buildGoogleApiClient();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Button don't do jack

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.d(LOG_TAG, "buildGoogleApiClient()");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // Override methods for Google Play Services
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG, "onConnected()");
        updateLocationData();

        if (mLastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            String ll = mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+","+mLastLocation.getAccuracy();
            Log.d(LOG_TAG, "Querying Yelp... ll = " + ll + " Search term: " + SEARCH_TERM);
            new YelpAsyncTask(ll, SEARCH_TERM).execute();                }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(LOG_TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    // Once connection with Google Play Services has been established, call this method to get location data
    private void updateLocationData(){
        Log.d(LOG_TAG, "updateLocationData()...");
        if (mGoogleApiClient.hasConnectedApi(LocationServices.API)) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {

                // I'm breaking a rule here.  Need to split data from UI update.
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                float accuracy = mLastLocation.getAccuracy();
                Log.d(LOG_TAG, "Success " + latitude + ", " + longitude + ", " + accuracy);
                mTextView_Latitude.setText(Double.toString(latitude));
                mTextView_Longitude.setText(Double.toString(longitude));
                mTextView_Accuracy.setText(Float.toString(accuracy) + " meters");
                Toast.makeText(this, "Location Data Updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(LOG_TAG, "No Location Detected");
                Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(LOG_TAG, "Location Services API not available");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocationData();
    }

    // Yelp stuff
    private class YelpAsyncTask extends AsyncTask<String, Void, String> {

        YelpAPI yelpApi = new YelpAPI(ApiKeys.CONSUMER_KEY, ApiKeys.CONSUMER_SECRET, ApiKeys.TOKEN, ApiKeys.TOKEN_SECRET);
        String userLocation;
        String userSearch;

        public YelpAsyncTask(String userLocation, String userSearch) {
            this.userLocation = userLocation;
            this.userSearch = userSearch;
        }

        @Override
        protected String doInBackground(String... strings) {
            return yelpApi.searchForBusinessesByLocation(userSearch, userLocation);
        }

        @Override
        protected void onPostExecute(String yelpResponse_Json) {
            super.onPostExecute(yelpResponse_Json);
            Log.d(LOG_TAG, yelpResponse_Json);
            mTextView_Other.setText(yelpResponse_Json);
            parseYelpResponse(yelpResponse_Json);
        }
    }

    void parseYelpResponse(String yelpResponse_Json){
        Log.d(LOG_TAG, "parseYelpResponse()");
        Gson gson = new Gson();
        YelpResponse yelpResponsePojo = gson.fromJson(yelpResponse_Json, YelpResponse.class);
        List<Business> businesses = yelpResponsePojo.getBusinesses();
        Business business = businesses.get(0);
        mTextView_Other.setText(business.getName() + "\nPhone: " + business.getPhone() + "\nWebsite: " + business.getUrl());
    }

    // MainActivity template menu override methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
