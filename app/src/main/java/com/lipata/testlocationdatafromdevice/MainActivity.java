package com.lipata.testlocationdatafromdevice;

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
import com.google.android.gms.location.LocationServices;

/**
 *  Quick and dirty test project that gets device location and other related data.  This data can
 *  be used to feed remote APIs, etc that provide data based on a user's location
 */

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    final String SEARCH_TERM = "restaurants";

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;

    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    protected TextView mAccuracyTextView;
    protected TextView mOtherLocationData;

    static final String LOG_TAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLatitudeTextView = (TextView) findViewById((R.id.latitude_text));
        mLongitudeTextView = (TextView) findViewById((R.id.longitude_text));
        mAccuracyTextView = (TextView) findViewById(R.id.accuracy_text);
        mOtherLocationData = (TextView) findViewById((R.id.otherlocationdata_text));

        buildGoogleApiClient();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Getting latest location data...", Toast.LENGTH_SHORT).show();
                updateLocationData();
                if (mLastLocation != null) {
                    String ll = mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+","+mLastLocation.getAccuracy();
                    Log.d(LOG_TAG, "Querying Yelp... ll = " + ll + " Search term: " + SEARCH_TERM);
                    new YelpAsyncTask(ll, SEARCH_TERM).execute();                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            float accuracy = mLastLocation.getAccuracy();
            Log.d(LOG_TAG, "Success " + latitude + ", " + longitude + ", " + accuracy);
            mLatitudeTextView.setText(Double.toString(latitude));
            mLongitudeTextView.setText(Double.toString(longitude));
            mAccuracyTextView.setText(Float.toString(accuracy) + " meters");
            Toast.makeText(this, "Location Data Updated", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "No Location Detected");
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
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
        protected void onPostExecute(String yelpResponse) {
            super.onPostExecute(yelpResponse);
            Log.d(LOG_TAG, yelpResponse);
            mOtherLocationData.setText(yelpResponse);
        }
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
