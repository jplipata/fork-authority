package com.lipata.forkauthority.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.gson.Gson;
import com.lipata.forkauthority.AppComponent;
import com.lipata.forkauthority.AppModule;
import com.lipata.forkauthority.DaggerAppComponent;
import com.lipata.forkauthority.R;
import com.lipata.forkauthority.api.GeocoderApi;
import com.lipata.forkauthority.api.GooglePlayApi;
import com.lipata.forkauthority.api.yelp3.YelpModule;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.data.AppSettings;
import com.lipata.forkauthority.data.ListComposer;
import com.lipata.forkauthority.data.user.UserRecords;
import com.lipata.forkauthority.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainView, BusinessListParentView {

    // Constants
    static final String LOCATION_UPDATE_TIMESTAMP_KEY = "mLocationUpdateTimestamp";
    static final String SUGGESTIONLIST_KEY = "suggestionList";
    static final String LOCATION_QUALITY_KEY = "locationQuality";
    static final String NO_RESULTS_TEXT_KEY = "noResultsText";
    static final String PROGRESS_BAR_BUSINESSES_KEY = "progressBarBusinesses";
    final static int MY_PERMISSIONS_ACCESS_FINE_LOCATION_ID = 0;

    // App modules
    AppComponent component;
    @Inject GeocoderApi mGeocoder;
    @Inject MainPresenter presenter;
    @Inject GooglePlayApi mGooglePlayApi;
    @Inject UserRecords mUserRecords;
    @Inject ListComposer listComposer;

    // Views
    protected CoordinatorLayout mCoordinatorLayout;
    protected TextView mTextView_ApproxLocation;
    protected RecyclerView mRecyclerView_suggestionList;
    private LinearLayoutManager mSuggestionListLayoutManager;
    private BusinessListAdapter mSuggestionListAdapter;
    FloatingActionButton mFAB_refresh;
    ObjectAnimator mFAB_refreshAnimation;
    Snackbar mSnackbar;
    FrameLayout mLayout_ProgressBar_Location;
    LocationQualityView mLocationQualityView;
    RelativeLayout mLayout_LocationViews;
    ProgressBar mProgressBar_Location;
    ProgressBar mProgressBar_Businesses;
    TextView mNoResultsTextView;
    ImageView mYelpLogo;

    // Analytics
    long mStartTime_Fetch;
    long mStartTime_Location;

    // Activity lifecycle

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component = DaggerAppComponent
                .builder()
                .appModule(new AppModule(getApplication()))
                .yelpModule(new YelpModule())
                .build();
        component.inject(this);

        Fabric.with(this, new Crashlytics());

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Answers())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter.setView(this);

        mCoordinatorLayout = findViewById(R.id.layout_coordinator);
        mTextView_ApproxLocation = findViewById(R.id.location_text);
        mLocationQualityView = new LocationQualityView(this, findViewById(R.id.accuracy_indicator));
        mLayout_LocationViews = findViewById(R.id.layout_location);
        mNoResultsTextView = findViewById(R.id.no_results);

        // Progress bar views
        mProgressBar_Location = findViewById(R.id.progress_bar_location);
        mProgressBar_Businesses = findViewById(R.id.progress_bar_businesses);
        mLayout_ProgressBar_Location = findViewById(R.id.layout_progress_bar_location);

        // RecyclerView
        mRecyclerView_suggestionList = findViewById(R.id.suggestion_list);
        mRecyclerView_suggestionList.setHasFixedSize(true);
        mSuggestionListLayoutManager = new LinearLayoutManager(this);
        mRecyclerView_suggestionList.setLayoutManager(mSuggestionListLayoutManager);
        RecyclerView.ItemAnimator animator = mRecyclerView_suggestionList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mSuggestionListAdapter = new BusinessListAdapter(this, mUserRecords);
        mRecyclerView_suggestionList.setAdapter(mSuggestionListAdapter);

        ItemTouchHelper.Callback callback = new ListItemTouchHelper(mSuggestionListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView_suggestionList);

        // Set up FAB and refresh animation
        mFAB_refresh = findViewById(R.id.fab);
        mFAB_refresh.setOnClickListener(view -> {
            if (mGooglePlayApi.isLocationStale()) {
                fetchBusinessList();
            } else {
                Toast.makeText(MainActivity.this, "Too soon. Please try again in a few seconds...", Toast.LENGTH_SHORT).show();
            }
        });
        mFAB_refreshAnimation = ObjectAnimator.ofFloat(mFAB_refresh, View.ROTATION, 360);
        mFAB_refreshAnimation.setDuration(1500);
        mFAB_refreshAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        mFAB_refreshAnimation.setInterpolator(null);

        // Clickable Yelp logo in compliance with Terms of Use
        // https://www.yelp.com/developers/display_requirements
        mYelpLogo = findViewById(R.id.yelp_logo);
        mYelpLogo.setOnClickListener(v -> openYelpDotCom());

        // Location API
        mGooglePlayApi.setActivity(this);

        // Restore state
        if (savedInstanceState != null) {
            mGooglePlayApi.setLocationUpdateTimestamp(savedInstanceState.getLong(LOCATION_UPDATE_TIMESTAMP_KEY));
            mLocationQualityView.setAccuracyCircleStatus(savedInstanceState.getInt(LOCATION_QUALITY_KEY));
            mNoResultsTextView.setVisibility(savedInstanceState.getInt(NO_RESULTS_TEXT_KEY));
            mProgressBar_Businesses.setVisibility(savedInstanceState.getInt(PROGRESS_BAR_BUSINESSES_KEY));

            //TODO Cache the list
//            String storedSuggestionList = savedInstanceState.getString(SUGGESTIONLIST_KEY, null);
//            if (storedSuggestionList != null) {
//                Type listType = new TypeToken<List<Business>>(){}.getType();
//                List<Business> retrievedBusinessList = new Gson().fromJson(storedSuggestionList, listType);
//                mSuggestionListAdapter.setListItems(retrievedBusinessList);
//            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check whether there are suggestion items in the RecyclerView.  If not, load some.
        if (mSuggestionListAdapter.getItemCount() == 0
                && mNoResultsTextView.getVisibility() != View.VISIBLE) {
            fetchBusinessList();
        }
    }

    @Override
    protected void onStop() {
        Timber.d("onStop()");
        super.onStop();
        if (mGooglePlayApi.getClient().isConnected()) {
            mGooglePlayApi.stopLocationUpdates();
        }
    }

    // UI methods

    @Override
    public void updateLocationViews(double latitude, double longitude, int accuracyQuality) {
        // Latitude range is 0 to +-90.  Longitude is 0 to +-180.
        // 6 decimal places is accurate to 43.496-111.32 mm
        // https://en.wikipedia.org/wiki/Decimal_degrees#Precision
        mTextView_ApproxLocation.setText(new DecimalFormat("##.######").format(latitude) + ", "
                + new DecimalFormat("###.######").format(longitude));
        mLocationQualityView.setAccuracyCircleStatus(accuracyQuality);
    }

    @Override
    public void startRefreshAnimation() {
        Timber.d("Starting animation");

        mProgressBar_Businesses.setVisibility(View.VISIBLE);

        if (!mFAB_refreshAnimation.isRunning()) {
            mFAB_refreshAnimation.start();
        }
    }

    @Override
    public void stopRefreshAnimation() {
        Timber.d("Stop animation");

        mProgressBar_Businesses.setVisibility(View.GONE);
        mProgressBar_Location.setVisibility(View.GONE);
        mFAB_refreshAnimation.cancel();
    }

    @Override
    public void showSnackBarIndefinite(String text) {
        mSnackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.show();
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLocationText(String text) {
        mTextView_ApproxLocation.setText(text);
    }


    /**
     * This gets called first, newBusinessList next
     */
    @Override
    public void onDeviceLocationRequested() {
        mStartTime_Location = System.nanoTime();

        mLayout_LocationViews.setVisibility(View.GONE);
        mLayout_ProgressBar_Location.setVisibility(View.VISIBLE);

        // Reset progress text for both business list and location
        mTextView_ApproxLocation.setText(getResources().getText(R.string.getting_your_location));

        mLocationQualityView.setAccuracyCircleStatus(LocationQualityView.Status.HIDDEN);
    }

    @Override
    public void onDeviceLocationRetrieved() {
        mLayout_ProgressBar_Location.setVisibility(View.GONE);
        mLayout_LocationViews.setVisibility(View.VISIBLE);

        Utility.reportExecutionTime(this, AppSettings.FABRIC_METRIC_GOOGLEPLAYAPI, mStartTime_Location);
        logFabricAnswersMetric(AppSettings.FABRIC_METRIC_GOOGLEPLAYAPI, mStartTime_Location);
    }

    @Override
    public void onNewBusinessListReceived() {
        // Analytics
        Utility.reportExecutionTime(this, "Fetch businesses until displayed", mStartTime_Fetch);
        logFabricAnswersMetric(AppSettings.FABRIC_METRIC_FETCH_BIZ_SEQUENCE, mStartTime_Fetch);
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

                    if (mGooglePlayApi.getClient().isConnected()) {
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

                        Timber.d("onActivityResult() RESULT_OK");
                        presenter.executeGooglePlayApiLocation();

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to

                        Timber.d("onActivityResult() RESULT_CANCELED");

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
    @Override
    public void fetchBusinessList() {
        mStartTime_Fetch = System.nanoTime();

        // UI

        // Dismiss any Snackbars
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }

        // Clear recyclerview
        mSuggestionListAdapter.setBusinessList(null);
        mSuggestionListAdapter.notifyDataSetChanged();

        mNoResultsTextView.setVisibility(View.GONE);

        startRefreshAnimation();

        presenter.onFetchBusinessList();
    }


    /**
     * Fabric Answers Custom Event
     *
     * @param metricName
     * @param startTime  In nanoseconds, will be converted to milliseconds
     */
    @Override
    public void logFabricAnswersMetric(String metricName, long startTime) {
        long executionTime = System.nanoTime() - startTime;
        long executionTime_ms = executionTime / 1000000;
        Answers.getInstance().logCustom(new CustomEvent(metricName)
                .putCustomAttribute("Execution time (ms)", executionTime_ms));
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(LOCATION_UPDATE_TIMESTAMP_KEY, mGooglePlayApi.getLocationUpdateTimestamp());
        savedInstanceState.putInt(LOCATION_QUALITY_KEY, mLocationQualityView.getStatus());
        savedInstanceState.putInt(NO_RESULTS_TEXT_KEY, mNoResultsTextView.getVisibility());
        savedInstanceState.putInt(PROGRESS_BAR_BUSINESSES_KEY, mProgressBar_Businesses.getVisibility());

        // TODO There must be a better way to do this
        String suggestionListStr = new Gson().toJson(mSuggestionListAdapter.getBusinessList());
        savedInstanceState.putString(SUGGESTIONLIST_KEY, suggestionListStr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
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

    @Override
    public boolean isNetworkConnected() {
        // Check for network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onNoResults() {
        mRecyclerView_suggestionList.setVisibility(View.GONE);
        mProgressBar_Businesses.setVisibility(View.GONE);
        mNoResultsTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSnackBarLongWithAction(
            final String message,
            final String actionLabel,
            final int position,
            final Business business) {

        mSnackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(actionLabel, v -> mSuggestionListAdapter.undoDismiss(position, business))
                .setActionTextColor(getResources().getColor(R.color.text_white));
        mSnackbar.show();
    }

    @Override
    public void showSnackBarLong(String text) {
        mSnackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }


    private void openYelpDotCom() {
        Uri webpage = Uri.parse("http://yelp.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Getters

    @Override
    public LinearLayoutManager getRecyclerViewLayoutManager() {
        return mSuggestionListLayoutManager;
    }

    @Override
    public BusinessListAdapter getSuggestionListAdapter() {
        return mSuggestionListAdapter;
    }

    public MainPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void notifyUserTooSoon(String businessName) {
        showSnackBarLong("Noted. You just ate at "
                + businessName
                + getString(R.string.moved_to_toosoon));
    }

    @Override
    public void notifyUserBusinessLiked(@NotNull String businessName) {
        showSnackBarLong("Noted. You like " + businessName
                + ". I have moved this to the top of the list."); // TODO Extract resource
    }

    @Override
    public void notifyUserBusinessDismissed(int position, @NotNull Business business) {
        showSnackBarLongWithAction(business.getName() + " dismissed.",
                "UNDO",
                position,
                business);
    }

    @Override
    public void notifyUserBusinessDontLiked(@NotNull String businessName) {
        showSnackBarLong("Noted. You don't like "
                + businessName
                + getString(R.string.moved_to_bottom));
    }

    @Override
    public void notifyNotAllowedOnDontLike() {
        showToast("Not allowed on a restaurant that you don't like.");
    }

    @Override
    public void launchBusinessUrl(@NotNull String url) {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @NotNull
    @Override
    public Drawable getRatingDrawable(@NotNull String rating) {
        switch (rating) {
            case "5.0":
                return getResources().getDrawable(R.drawable.stars_small_5);
            case "4.5":
                return getResources().getDrawable(R.drawable.stars_small_4_half);
            case "4.0":
                return getResources().getDrawable(R.drawable.stars_small_4);
            case "3.5":
                return getResources().getDrawable(R.drawable.stars_small_3_half);
            case "3.0":
                return getResources().getDrawable(R.drawable.stars_small_3);
            case "2.5":
                return getResources().getDrawable(R.drawable.stars_small_2_half);
            case "2.0":
                return getResources().getDrawable(R.drawable.stars_small_2);
            case "1.5":
                return getResources().getDrawable(R.drawable.stars_small_1_half);
            case "1.0":
                return getResources().getDrawable(R.drawable.stars_small_1);
            default:
                return getResources().getDrawable(R.drawable.stars_small_0);
        }
    }
}
