package com.lipata.whatsforlunch;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.lipata.whatsforlunch.api.DeviceLocation;
import com.lipata.whatsforlunch.api.yelp.AsyncYelpCall;
import com.lipata.whatsforlunch.data.AppSettings;
import com.lipata.whatsforlunch.data.BusinessListManager;
import com.lipata.whatsforlunch.data.user.UserRecords;
import com.lipata.whatsforlunch.data.yelppojo.Business;

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
    protected TextView mTextView_Latitude;
    protected TextView mTextView_Longitude;
    protected TextView mTextView_Accuracy;
    protected RecyclerView mRecyclerView_suggestionList;
    private RecyclerView.LayoutManager mSuggestionListLayoutManager;
    private BusinessListAdapter mSuggestionListAdapter;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    FloatingActionButton mFAB_refresh;
    ObjectAnimator mFAB_refreshAnimation;

    DeviceLocation deviceLocation;
    UserRecords mUserRecords;
    BusinessListManager mBusinessListManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserRecords = new UserRecords(this);
        mBusinessListManager = new BusinessListManager(this, mUserRecords);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_coordinator);
        mTextView_Latitude = (TextView) findViewById((R.id.latitude_text));
        mTextView_Longitude = (TextView) findViewById((R.id.longitude_text));
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

        // Temporarily disabling swipe to refresh in lieu of FAB
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
//        mSwipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.d(LOG_TAG, "Pulldown refresh.  onRefresh()");
//                if (isLocationStale()) {
//                    executeSequence();
//                } else {
//                    Toast.makeText(MainActivity.this, "Too soon. Please try again in a few seconds...", Toast.LENGTH_SHORT).show();
//                }
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });


        // Set up FAB and refresh animation
        mFAB_refresh = (FloatingActionButton) findViewById(R.id.fab);
        mFAB_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceLocation.isLocationStale()) {
                    executeSequence();
                } else {
                    Toast.makeText(MainActivity.this, "Too soon. Please try again in a few seconds...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mFAB_refreshAnimation = ObjectAnimator.ofFloat(mFAB_refresh, View.ROTATION, 360);
        mFAB_refreshAnimation.setDuration(1500);
        mFAB_refreshAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        mFAB_refreshAnimation.setInterpolator(null);

        // Location stuff
        deviceLocation = new DeviceLocation(this);
        deviceLocation.initialize();

        // Restore state
        if (savedInstanceState != null) {
            deviceLocation.setLocationUpdateTimestamp(savedInstanceState.getLong(LOCATION_UPDATE_TIMESTAMP_KEY));

            String storedSuggestionList = savedInstanceState.getString(SUGGESTIONLIST_KEY, null);
            if (storedSuggestionList != null) {
                Type listType = new TypeToken<List<Business>>(){}.getType();
                List<Business> retrievedBusinessList = new Gson().fromJson(storedSuggestionList, listType);
                mSuggestionListAdapter.setBusinessList(retrievedBusinessList);
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Check whether there are suggestion items in the RecyclerView.  If not, load some.
        if(mSuggestionListAdapter.getItemCount()==0){
               deviceLocation.getClient().connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        if (deviceLocation.getClient().isConnected()) {
            deviceLocation.stopLocationUpdates();
        }
    }

    // UI methods

    public void updateLocationViews(double latitude, double longitude, float accuracy){
        mTextView_Latitude.setText(Double.toString(latitude));
        mTextView_Longitude.setText(Double.toString(longitude));
        mTextView_Accuracy.setText(Float.toString(accuracy) + " meters");
        //Toast.makeText(this, "Location Data Updated", Toast.LENGTH_SHORT).show();
    }

    public void stopRefreshAnimation(){
        Log.d(LOG_TAG, "Stop animation");
        //mRefreshAnimation.cancel();
        mFAB_refreshAnimation.cancel();
    }

    public void onGoogleApiConnectionFailed(int errorCode){

        // TODO This logic should probably live somewhere else, e.g. Presenter.  Maybe it's better
        // to just have a `public display(String textToBeDisplayed)` and have the logic handled elsewhere

        switch (errorCode){
            case 1:
                Snackbar.make(mCoordinatorLayout,
                        "ERROR: Google Play services is missing on this device",
                        Snackbar.LENGTH_INDEFINITE).show();
                break;
            case 2:
                Snackbar.make(mCoordinatorLayout,
                        "ERROR: The installed version of Google Play services is out of date.",
                        Snackbar.LENGTH_INDEFINITE).show();
                break;
            default:
                Snackbar.make(mCoordinatorLayout,
                        "ERROR: Google API Client, error code: " + errorCode,
                        Snackbar.LENGTH_INDEFINITE).show();
                break;
        }
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

                    if(deviceLocation.getClient().isConnected()) {
                        deviceLocation.requestLocationUpdates();
                    } else {
                        deviceLocation.getClient().connect();
                    }

                } else {
                    Snackbar.make(mCoordinatorLayout, "Location Permission Required", Snackbar.LENGTH_LONG).show();                }
                return;
            }
        }
    }

    // Retain Activity state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(LOCATION_UPDATE_TIMESTAMP_KEY, deviceLocation.getLocationUpdateTimestamp());

        // TODO Should this be done with a Parcelable instead?
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
    public RecyclerView.LayoutManager getRecyclerViewLayoutManager(){
        return mSuggestionListLayoutManager;
    }

    public CoordinatorLayout getCoordinatorLayout(){
        return mCoordinatorLayout;
    }

    public BusinessListAdapter getSuggestionListAdapter() {
        return mSuggestionListAdapter;
    }

    // Business Logic
    // TODO This should be handled somewhere else
    public void executeSequence(){
        final Toast toast = Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT);
        toast.show();
        Log.d(LOG_TAG, "Starting animation");
        if(!mFAB_refreshAnimation.isRunning()) {
            mFAB_refreshAnimation.start();
        }

        deviceLocation.showLocation();

        // If getLastLocation() returned null, start a Location Request to get device location
        // Else, query yelp with existing location arguments
        if (deviceLocation.getLastLocation() == null || deviceLocation.isLocationStale()) {
            deviceLocation.requestLocationData();
        } else {
            // Check for network connectivity
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            // If connected to network make Yelp API call, if no network, notify user
            if(isConnected) {
                String ll = deviceLocation.getLastLocation().getLatitude() + ","
                        + deviceLocation.getLastLocation().getLongitude() + ","
                        + deviceLocation.getLastLocation().getAccuracy();
                Log.d(LOG_TAG, "Querying Yelp... ll = " + ll + " Search term: " + AppSettings.SEARCH_TERM);
                new AsyncYelpCall(ll, AppSettings.SEARCH_TERM, mBusinessListManager, this, toast).execute();
            } else {
                Snackbar.make(mCoordinatorLayout, "No network. Try again when you are connected to the internet.",
                        Snackbar.LENGTH_INDEFINITE).show();
                stopRefreshAnimation();
            }
        }
    }

}
