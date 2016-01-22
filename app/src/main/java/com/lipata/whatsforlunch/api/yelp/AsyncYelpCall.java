package com.lipata.whatsforlunch.api.yelp;

import android.os.AsyncTask;
import android.util.Log;

import com.lipata.whatsforlunch.ApiKeys;
import com.lipata.whatsforlunch.MainActivity;

/**
 * Created by jlipatap on 1/21/16.
 */
public class AsyncYelpCall extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = AsyncYelpCall.class.getSimpleName();
    YelpAPI yelpApi = new YelpAPI(ApiKeys.CONSUMER_KEY, ApiKeys.CONSUMER_SECRET, ApiKeys.TOKEN, ApiKeys.TOKEN_SECRET);
    String userLocation;
    String userSearch;
    MainActivity mMainAcitivity;

    public AsyncYelpCall(String userLocation, String userSearch, MainActivity mainActivity) {
        this.userLocation = userLocation;
        this.userSearch = userSearch;
        this.mMainAcitivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        return yelpApi.searchForBusinessesByLocation(userSearch, userLocation);
    }

    @Override
    protected void onPostExecute(String yelpResponse_Json) {
        super.onPostExecute(yelpResponse_Json);
        Log.d(LOG_TAG, yelpResponse_Json);
        mMainAcitivity.parseYelpResponse(yelpResponse_Json);
    }
}
