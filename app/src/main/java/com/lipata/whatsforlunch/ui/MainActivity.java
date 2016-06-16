package com.lipata.whatsforlunch.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lipata.whatsforlunch.R;
import com.lipata.whatsforlunch.api.GooglePlayApi;
import com.lipata.whatsforlunch.api.MyGeocoder;
import com.lipata.whatsforlunch.api.yelp.model.Business;
import com.lipata.whatsforlunch.data.BusinessListManager;
import com.lipata.whatsforlunch.data.user.UserRecords;

import java.lang.reflect.Type;
import java.util.List;

/**
 *  This Android app gets device location, queries the Yelp API for restaurant recommendations,
 *  and uses GSON to parse and display the response.
 */

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Constants
    static final String LOCATION_UPDATE_TIMESTAMP_KEY = "mLocationUpdateTimestamp"; // TODO: This should go in R.strings
    static final String SUGGESTIONLIST_KEY = "suggestionList"; // TODO: This should go in R.strings
    static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID = 0;

    // Views
    protected CoordinatorLayout mCoordinatorLayout;
    //protected TextView mTextView_Latitude;
    //protected TextView mTextView_Longitude;
    protected TextView mTextView_ApproxLocation;
    protected TextView mTextView_Accuracy;
    protected RecyclerView mRecyclerView_suggestionList;
    private LinearLayoutManager mSuggestionListLayoutManager;
    private BusinessListAdapter mSuggestionListAdapter;
    FloatingActionButton mFAB_refresh;
    ObjectAnimator mFAB_refreshAnimation;
    Snackbar mSnackbar;

    // App modules
    GooglePlayApi mGooglePlayApi;
    MyGeocoder mGeocoder;
    UserRecords mUserRecords;
    BusinessListManager mBusinessListManager;

    // Activity lifecycle

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserRecords = new UserRecords(this);
        mBusinessListManager = new BusinessListManager(this, mUserRecords);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_coordinator);
        //mTextView_Latitude = (TextView) findViewById((R.id.latitude_text));
        //mTextView_Longitude = (TextView) findViewById((R.id.longitude_text));
        mTextView_ApproxLocation = (TextView) findViewById(R.id.approx_location_text);
        mTextView_Accuracy = (TextView) findViewById(R.id.accuracy_text);

        // RecyclerView
        mRecyclerView_suggestionList = (RecyclerView) findViewById(R.id.suggestion_list);
        mRecyclerView_suggestionList.setHasFixedSize(true);
        mSuggestionListLayoutManager = new LinearLayoutManager(this);
        mRecyclerView_suggestionList.setLayoutManager(mSuggestionListLayoutManager);

        mSuggestionListAdapter = new BusinessListAdapter(this, mUserRecords, mBusinessListManager);
        mRecyclerView_suggestionList.setAdapter(mSuggestionListAdapter);

        ItemTouchHelper.Callback callback = new ListItemTouchHelper(mSuggestionListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView_suggestionList);

        mRecyclerView_suggestionList.addOnScrollListener(new BusinessListScrollListener(mSuggestionListLayoutManager));


        // Set up FAB and refresh animation
        mFAB_refresh = (FloatingActionButton) findViewById(R.id.fab);
        mFAB_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGooglePlayApi.isLocationStale()) {
                    fetchBusinessList();
                } else {
                    Toast.makeText(MainActivity.this, "Too soon. Please try again in a few seconds...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mFAB_refreshAnimation = ObjectAnimator.ofFloat(mFAB_refresh, View.ROTATION, 360);
        mFAB_refreshAnimation.setDuration(1500);
        mFAB_refreshAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        mFAB_refreshAnimation.setInterpolator(null);

        // Location API
        mGeocoder = new MyGeocoder(this);
        mGooglePlayApi = new GooglePlayApi(this, mGeocoder);

        // Restore state
        if (savedInstanceState != null) {
            mGooglePlayApi.setLocationUpdateTimestamp(savedInstanceState.getLong(LOCATION_UPDATE_TIMESTAMP_KEY));

            String storedSuggestionList = savedInstanceState.getString(SUGGESTIONLIST_KEY, null);
            if (storedSuggestionList != null) {
                Type listType = new TypeToken<List<Business>>(){}.getType();
                List<Business> retrievedBusinessList = new Gson().fromJson(storedSuggestionList, listType);
                mSuggestionListAdapter.setBusinessList(retrievedBusinessList);
            }
        }
    }

    @Override protected void onStart(){
        super.onStart();

        // Check whether there are suggestion items in the RecyclerView.  If not, load some.
        if(mSuggestionListAdapter.getItemCount()==0){
            fetchBusinessList();
        }


    }

    @Override protected void onResume(){
        Log.d(LOG_TAG, "onResume()");
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");

    }

    @Override protected void onStop(){
        Log.d(LOG_TAG, "onStop()");
        super.onStop();
        if (mGooglePlayApi.getClient().isConnected()) {
            mGooglePlayApi.stopLocationUpdates();
        }

    }

    // UI methods

    public void updateLocationViews(double latitude, double longitude, float accuracy){
        //mTextView_Latitude.setText(Double.toString(latitude));
        //mTextView_Longitude.setText(Double.toString(longitude));
        mTextView_Accuracy.setText(Float.toString(accuracy) + " meters");
        //Toast.makeText(this, "Location Data Updated", Toast.LENGTH_SHORT).show();
    }

    public void startRefreshAnimation(){
        Log.d(LOG_TAG, "Starting animation");
        if(!mFAB_refreshAnimation.isRunning()) {
            mFAB_refreshAnimation.start();
        }
    }

    public void stopRefreshAnimation(){
        Log.d(LOG_TAG, "Stop animation");
        //mRefreshAnimation.cancel();
        mFAB_refreshAnimation.cancel();
    }

    public void showSnackBarIndefinite(String text){
        mSnackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.show();
    }

    public void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public void setApproxLocation(String text){
        mTextView_ApproxLocation.setText(text);
    }

    // Callback for Marshmallow requestPermissions() response
    // This must live in the Activity class
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(mGooglePlayApi.getClient().isConnected()) {
                        mGooglePlayApi.requestLocationUpdates();
                    } else {
                        mGooglePlayApi.getClient().connect();
                    }

                } else {
                    stopRefreshAnimation();
                    showSnackBarIndefinite("Location permission required");
                }
                return;
            }
        }
    }

    // Callback for GooglePlayApi Settings API
    // This must live in the Activity class
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GooglePlayApi.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        Log.d(LOG_TAG, "onActivityResult() RESULT_OK");
                        executeGooglePlayApiLocation();

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to

                        Log.d(LOG_TAG, "onActivityResult() RESULT_CANCELED");

                        stopRefreshAnimation();
                        showSnackBarIndefinite("Location settings error");

                        break;
                    default:
                        break;
                }
                break;
        }
    }


    // Trigger location + yelp calls
    public void fetchBusinessList(){

        // UI

            // Wrote this Toast to have a reference so that it could be cancelled once operation completes.
            // However, I called cancel() with no noticeable effect.  Keeping this code in case I
            // figure it out later.
            final Toast toast = Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT);
            toast.show();

            // Dismiss any Snackbars
            if(mSnackbar!=null){
                mSnackbar.dismiss();
            }

            startRefreshAnimation();

        // Business Logic


            // If the location has already been recently updated, no need to update it, go straight to quarying yelp

            if(!mGooglePlayApi.isLocationStale()) {

                mGooglePlayApi.checkNetworkPermissionAndCallYelpApi();

            } else {
                // Connect to GooglePlayApi, which will trigger onConnect() callback, i.e. execute sequence of events

                executeGooglePlayApiLocation();
            }

    }


    // Helper methods

    private void executeGooglePlayApiLocation(){
        if(!mGooglePlayApi.getClient().isConnected()){
            mGooglePlayApi.getClient().connect();
        } else {
            mGooglePlayApi.checkDeviceLocationEnabled();
        }
    }


    // Retain Activity state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(LOCATION_UPDATE_TIMESTAMP_KEY, mGooglePlayApi.getLocationUpdateTimestamp());

        // TODO There must be a better way to do this
        String suggestionListStr = new Gson().toJson(mSuggestionListAdapter.getBusinessList());
        savedInstanceState.putString(SUGGESTIONLIST_KEY, suggestionListStr);
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


    // Getters

    //TODO RecyclerView.LayoutManager has been replaced by android.support.v7.widget.LinearLayoutManager.  For some reason this still works, but it could cause problems later.
    public RecyclerView.LayoutManager getRecyclerViewLayoutManager(){
        return mSuggestionListLayoutManager;
    }

    public CoordinatorLayout getCoordinatorLayout(){
        return mCoordinatorLayout;
    }

    public BusinessListAdapter getSuggestionListAdapter() {
        return mSuggestionListAdapter;
    }

    public BusinessListManager getBusinessListManager() {
        return mBusinessListManager;
    }

}
