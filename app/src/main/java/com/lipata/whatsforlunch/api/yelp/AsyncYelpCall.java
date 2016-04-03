package com.lipata.whatsforlunch.api.yelp;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.lipata.whatsforlunch.ApiKeys;
import com.lipata.whatsforlunch.BusinessListAdapter;
import com.lipata.whatsforlunch.MainActivity;
import com.lipata.whatsforlunch.data.BusinessListManager;
import com.lipata.whatsforlunch.data.AppSettings;
import com.lipata.whatsforlunch.data.yelppojo.Business;
import com.lipata.whatsforlunch.data.yelppojo.YelpResponse;

import java.util.List;

/**
 * Created by jlipatap on 1/21/16.
 * TODO Replace this AsyncTask with Retrofit
 */

public class AsyncYelpCall extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = AsyncYelpCall.class.getSimpleName();
    YelpAPI mYelpApi = new YelpAPI(ApiKeys.CONSUMER_KEY, ApiKeys.CONSUMER_SECRET, ApiKeys.TOKEN, ApiKeys.TOKEN_SECRET);
    String mUserLocation;
    String mUserSearch;
    BusinessListManager mBusinessListManager;
    BusinessListAdapter mBusinessListAdapter;
    MainActivity mMainActivity;
    Toast mToast;

    public AsyncYelpCall(String userLocation, String userSearch, BusinessListManager businessListManager,
                         BusinessListAdapter businessListAdapter, MainActivity mainActivity, Toast toast) {
        this.mUserLocation = userLocation;
        this.mUserSearch = userSearch;
        this.mBusinessListManager = businessListManager;
        this.mBusinessListAdapter = businessListAdapter;
        this.mMainActivity = mainActivity;
        this.mToast = toast;

    }

    @Override
    protected String doInBackground(String... strings) {

        String yelpResponse = "";

        try {
            yelpResponse = mYelpApi.searchForBusinessesByLocation(mUserSearch, mUserLocation, AppSettings.SEARCH_RADIUS);

        } catch (Exception e) {
            Log.e(LOG_TAG, "AsyncTask Error: " + e.toString());
        }

        return yelpResponse;
    }

    @Override
    protected void onPostExecute(String yelpResponse_Json) {

        super.onPostExecute(yelpResponse_Json);
        Log.d(LOG_TAG, yelpResponse_Json);

        YelpResponse yelpResponse = mYelpApi.parseYelpResponse(yelpResponse_Json);
        List<Business> businessList = yelpResponse.getBusinesses();

        // Handle Yelp API "error"
        if(yelpResponse.getError()!=null){
            Log.e(LOG_TAG, "YELP API ERROR RETURNED: " + yelpResponse.getError().getText()
                    + " ID: " + yelpResponse.getError().getId());
            Snackbar.make(mMainActivity.getCoordinatorLayout(), "Yelp API Error: "
                    +  yelpResponse.getError().getText(), Snackbar.LENGTH_INDEFINITE).show();
        }

        // Handle case where there's no error but no results are returned
        // I have not encountered this case, but imagine that it could happen
        else if (businessList.size()==0) {
            Log.d(LOG_TAG, "Yelp API returned no results");
            Snackbar.make(mMainActivity.getCoordinatorLayout(), "No businesses found.", Snackbar.LENGTH_INDEFINITE).show();
        }

        // Handle expected results
        else {
            // Manipulate `businessList` to apply customization
            List<Business> filteredBusinesses = mBusinessListManager.filter(businessList);
            mBusinessListAdapter.setBusinessList(filteredBusinesses);
            mBusinessListAdapter.notifyDataSetChanged();
        }

        // UI stuff -- This probably shouldn't be here
        mMainActivity.stopRefreshAnimation();
        mToast.cancel();
        mMainActivity.getRecyclerViewLayoutManager().scrollToPosition(0);
    }
}
