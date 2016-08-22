package com.lipata.whatsforlunch.api.yelp;

import android.util.Log;

import com.lipata.whatsforlunch.BuildConfig;
import com.lipata.whatsforlunch.Utility;
import com.lipata.whatsforlunch.api.yelp.model.Business;
import com.lipata.whatsforlunch.api.yelp.model.YelpResponse;
import com.lipata.whatsforlunch.ui.BusinessListAdapter;
import com.lipata.whatsforlunch.ui.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Created by jlipata on 5/15/16.
 */
public class YelpApi {

    public static final String LOG_TAG = "YelpApi";
    public static final String BASE_URL = "https://api.yelp.com/";
    public static final int RESULTS_PER_PAGE = 20; // However many results the Yelp API returns per page
    public static final int MAX_NO_OF_RESULTS = 200; // Max number of businesses that will be fetched from Yelp

    OkHttpOAuthConsumer mConsumer;
    HttpLoggingInterceptor mHttpLoggingInterceptor;
    OkHttpClient mOkHttpClient;
    Retrofit mRetrofit;
    final Endpoints mApiService;

    // TODO Need to abstract this
    MainActivity mMainActivity;

    List<Business> mMasterList; // This is our main data
    int mTotalNumberOfResults;

    /**
     * CallLog
     * Uses the `offset` as the key and the boolean to track whether the call has been received.
     * When we add an entry to the log, we set it to false, meaning the response has not been received yet.
     * The call2 callback will set the value to `true` when the response callback has been called.
     */
    HashMap<Integer, Boolean> mCallLog;

    long mCallYelpApiStartTime;

    public YelpApi(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;

        // OAuth
        mConsumer = new OkHttpOAuthConsumer(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);
        mConsumer.setTokenWithSecret(BuildConfig.TOKEN, BuildConfig.TOKEN_SECRET);

        // Logger
        mHttpLoggingInterceptor = new HttpLoggingInterceptor();
        mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(mConsumer))
                .addInterceptor(mHttpLoggingInterceptor) // As per tutorial: We recommend to add logging as the last interceptor, because this will also log the information which you added with previous interceptors to your request.
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = mRetrofit.create(Endpoints.class);
    }

    public void callYelpApi(final String term, final String location, final String radius){
        mCallYelpApiStartTime = System.nanoTime();

        // Call Yelp
        Call<YelpResponse> call = mApiService.getBusinesses(term, location, radius);
        call.enqueue(new Callback<YelpResponse>() {
            @Override
            public void onResponse(Call<YelpResponse> call, Response<YelpResponse> response) {

                final YelpResponse yelpResponse = response.body();

                if (yelpResponse != null) {
                    // Handle Yelp API "error"
                    if (yelpResponse.getError() != null) {
                        Log.e(LOG_TAG, "YELP API ERROR RETURNED: " + yelpResponse.getError().getText()
                                + " ID: " + yelpResponse.getError().getId());
                        mMainActivity.stopRefreshAnimation();
                        mMainActivity.showSnackBarIndefinite("Yelp API Error: " + yelpResponse.getError().getText());
                    }

                    // Handle case where there's no error but no results are returned
                    else if (yelpResponse.getBusinesses().size() == 0) {
                        Log.d(LOG_TAG, "Yelp API returned no results");
                        mMainActivity.stopRefreshAnimation();
                        mMainActivity.showSnackBarIndefinite("No businesses found.");
                    }

                    // Handle case where there are 20 or less results
                    else if (yelpResponse.getTotal() <= 20) {
                        mMasterList.addAll(yelpResponse.getBusinesses());
                        mTotalNumberOfResults = yelpResponse.getTotal();
                        filterListAndUpdateUi();
                    }

                    // Handle more than 20 results
                    else {
                        getMoreThan20Results(yelpResponse, term, location, radius);
                    }
                } else {
                    mMainActivity.showSnackBarIndefinite("Yelp API Error: Null response.");
                    mMainActivity.stopRefreshAnimation();
                }
            }

            @Override
            public void onFailure(Call<YelpResponse> call, Throwable t) {
                Log.e(LOG_TAG, "Retrofit FAILURE", t);
                t.printStackTrace();
                mMainActivity.stopRefreshAnimation();
                mMainActivity.showSnackBarIndefinite("Yelp API error.  Check your internet connection or perhaps there's a problem with Yelp at the moment.");
            }
        });
    }

    //TODO We really don't need 1000 results, perhaps limit to say 500
    private void getMoreThan20Results(final YelpResponse yelpResponse, String term, String location, String radius) {

        // Reset mMasterList & mCallLog
        mMasterList = new ArrayList<>();
        mCallLog = new HashMap<>();

        // Using an array `businessArray` so that we can use the indexes to keep the results in order since
        // they will be received asynchronously.  Created according to the size returned by the initial Yelp response
        // TODO There will be a problem in any cases where Yelp returns results in excess of the `total` defined in the first call. Replace with List?
        mTotalNumberOfResults = yelpResponse.getTotal();
        final Business[] businessArray = new Business[mTotalNumberOfResults];

        mMainActivity.showToast(String.format("Retrieving %d businesses...", mTotalNumberOfResults));

        // Load the first 20
        for (int i = 0; i<yelpResponse.getBusinesses().size() ; i++){
            businessArray[i] = yelpResponse.getBusinesses().get(i);
        }

        // Load the rest
        final int start = yelpResponse.getBusinesses().size();

        // Let's set the max to MAX_NO_OF_RESULTS and see how it performs
        for(int offsetInt = start; offsetInt< mTotalNumberOfResults && offsetInt<MAX_NO_OF_RESULTS ; /* i is updated below */ ){
            String offsetStr = Integer.toString(offsetInt);
            final int offsetPointer = offsetInt; // Need a `final` variable to use in the anonymous class below, otherwise I would just use `i`

            // Add the call, identified by the offset, to the CallLog.  `false` means the response hasn't been received
            // This will be updated to `true` by the callback below when a response is received
            mCallLog.put(offsetInt, false);
            Call<YelpResponse> call2 = mApiService.getBusinesses(term, location, radius, offsetStr);
            call2.enqueue(new Callback<YelpResponse>() {
                @Override
                public void onResponse(Call<YelpResponse> call, Response<YelpResponse> response) {
                    mCallLog.put(offsetPointer, true);
                    YelpResponse yelpResponse2 = response.body();

                    // For every possible outcome of this `if else` tree, we need to call `tryUpdateMasterListandUpdateUI()`
                    // Otherwise, the program will never proceed.
                    if (yelpResponse2!=null) {
                        // Handle Yelp API "error"
                        if (yelpResponse2.getError() != null) {
                            Log.e(LOG_TAG, "YELP API ERROR RETURNED: " + yelpResponse.getError().getText()
                                    + " ID: " + yelpResponse.getError().getId());
                            tryUpdateMasterListandUpdateUI(businessArray);
                        }

                        // Handle case where 0 businesses are returned
                        else if (yelpResponse2.getBusinesses().size()==0){
                            Log.d(LOG_TAG, String.format("Offset %d , Yelp API returned no results", offsetPointer));
                            tryUpdateMasterListandUpdateUI(businessArray);
                        }
                        // If there's no error, proceed to add businesses
                        else {
                            List<Business> businesses = yelpResponse2.getBusinesses();
                            addBusinesses(offsetPointer, businesses, businessArray);
                        }
                    } else if (response.errorBody()!=null){
                        try{
                            String error = response.errorBody().string();
                            Log.d(LOG_TAG, error);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        tryUpdateMasterListandUpdateUI(businessArray);
                    }


                    // This is one possible solution
                    /*
                    try {
                        List<Business> businesses = yelpResponse2.getBusinesses();
                        addBusinesses(offsetPointer, businesses, businessArray);
                    }
                    catch (Exception e){
                        Log.e(LOG_TAG, "call2 onResponse() ERROR");
                        e.printStackTrace();
                    }
                    */
                }

                @Override
                public void onFailure(Call<YelpResponse> call, Throwable t) {
                    t.printStackTrace();
                    tryUpdateMasterListandUpdateUI(businessArray);
                }
            });

            // Update `i`
            // We cannot increment `i` by the actual number of responses received because the response
            // is received asynchronously, i.e. we don't have the data at this time
            offsetInt=offsetInt+RESULTS_PER_PAGE;
        }
    }

    private void addBusinesses(int offset, List<Business> businessList, Business[] businessArray){
        // Add new batch of businesses to the master array in the correct order
        for(int i=0; i<businessList.size(); i++){
            businessArray[offset+i] = businessList.get(i);
        }

        // Check to see if all calls have been received.  If yes, proceed to filter list and update UI
        tryUpdateMasterListandUpdateUI(businessArray);
    }

    private void tryUpdateMasterListandUpdateUI(Business[] businessArray) {
        // TODO DANGER! If one of the callbacks never gets called, this method might not ever get called and the program will not proceed
        if(areAllCallsReceived()){
            mMasterList.clear();
            mMasterList.addAll(Arrays.asList(businessArray));
            filterListAndUpdateUi();
        } else {
            Log.d(LOG_TAG, "areAllCallsReceived = false");
        }
    }

    /**
     * This method iterates through mCallLog, looking for all `true` values.  If all values are true,
     * then all `call2` responses have been received
     *
     * @return  True if all call2 responses have been received.  False if not
     */
    private boolean areAllCallsReceived(){
        long startTime = System.nanoTime();
        for(Map.Entry<Integer, Boolean> entry : mCallLog.entrySet()){
            if(entry.getValue()==false){
                //Utility.reportExecutionTime(this, "areAllCallsReceived() FALSE",startTime);
                return false;
            }
        }
        //Utility.reportExecutionTime(this, "areAllCallsReceived() TRUE", startTime);
        return true;
    }

    private void filterListAndUpdateUi() {
        // There might be null values if the Yelp Api returns fewer actual results than specified in the response
        // `total` field.  Therefore we should remove any possible null values before passing to the UI
        mMasterList.removeAll(Collections.singleton(null));
        Log.d(LOG_TAG, "Total results received " + mMasterList.size());

        // Pass list to BusinessListManager to be processed and update UI
        BusinessListAdapter businessListAdapter = mMainActivity.getSuggestionListAdapter();
        List<Business> filteredBusinesses = mMainActivity.getBusinessListManager().filter(mMasterList);
        businessListAdapter.setBusinessList(filteredBusinesses);
        businessListAdapter.notifyDataSetChanged();
        mMainActivity.stopRefreshAnimation();
        mMainActivity.getRecyclerViewLayoutManager().scrollToPosition(0);
        Utility.reportExecutionTime(this, "callYelpApi sequence, time to get "+mMasterList.size()+" businesses", mCallYelpApiStartTime);
        mMainActivity.onKeyMetric("YelpApi call", mCallYelpApiStartTime);
    }

}
