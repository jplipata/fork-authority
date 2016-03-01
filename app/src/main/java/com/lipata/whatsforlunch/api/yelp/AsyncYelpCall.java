package com.lipata.whatsforlunch.api.yelp;

import android.os.AsyncTask;
import android.util.Log;

import com.lipata.whatsforlunch.ApiKeys;
import com.lipata.whatsforlunch.BusinessListAdapter;
import com.lipata.whatsforlunch.data.BusinessListManager;
import com.lipata.whatsforlunch.data.AppSettings;
import com.lipata.whatsforlunch.data.yelppojo.Business;

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

    public AsyncYelpCall(String userLocation, String userSearch, BusinessListManager businessListManager,
                         BusinessListAdapter mBusinessListAdapter) {
        this.mUserLocation = userLocation;
        this.mUserSearch = userSearch;
        this.mBusinessListManager = businessListManager;
        this.mBusinessListAdapter = mBusinessListAdapter;

    }

    @Override
    protected String doInBackground(String... strings) {
        return mYelpApi.searchForBusinessesByLocation(mUserSearch, mUserLocation, AppSettings.SEARCH_RADIUS);
    }

    @Override
    protected void onPostExecute(String yelpResponse_Json) {
        super.onPostExecute(yelpResponse_Json);
        Log.d(LOG_TAG, yelpResponse_Json);
        List<Business> businessList = mYelpApi.parseYelpResponse(yelpResponse_Json);

        // Manipulate `businessList` to apply customization
        List<Business> filteredBusinesses = mBusinessListManager.filter(businessList);
        mBusinessListAdapter.setBusinessList(filteredBusinesses);
        mBusinessListAdapter.notifyDataSetChanged();
    }
}
