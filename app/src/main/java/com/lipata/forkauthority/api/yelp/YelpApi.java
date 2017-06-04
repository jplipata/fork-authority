package com.lipata.forkauthority.api.yelp;

import android.util.Log;

import com.lipata.forkauthority.BuildConfig;
import com.lipata.forkauthority.Utility;
import com.lipata.forkauthority.api.yelp.entities.Business;
import com.lipata.forkauthority.api.yelp.entities.YelpResponse;
import com.lipata.forkauthority.data.AppSettings;
import com.lipata.forkauthority.ui.BusinessListAdapter;
import com.lipata.forkauthority.ui.MainView;

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

public class YelpApi {
    public static final String LOG_TAG = "YelpApi";
    public static final String BASE_URL = "https://api.yelp.com/";
    public static final int RESULTS_PER_PAGE = 20; // However many results the Yelp API returns per page

    /**
     * Amount by which to update the progress bar when making initial Yelp Api call
     */
    public static final int INITIAL_YELPAPICALL_PROGRESS_VALUE = 10;

    // Retrofit stuff
    OkHttpOAuthConsumer mConsumer;
    HttpLoggingInterceptor mHttpLoggingInterceptor;
    OkHttpClient mOkHttpClient;
    Retrofit mRetrofit;
    final Endpoints mApiService;

    MainView view;

    List<Business> mMasterList; // This is our main data

    // TODO This will need to be cleaned up -- too many variables
    int mTotalResultsAsPerFirstYelpCall;
    int mActionableResults;

    /**
     * mCallLog
     * Uses the `offset` as the key and the boolean to track whether the call has been received.
     * When we add an entry to the log, we set it to false, meaning the response has not been received yet.
     * The call2 callback will set the value to `true` when the response callback has been called.
     */
    HashMap<Integer, Boolean> mCallLog;

    long mCallYelpApiStartTime;

    public YelpApi(MainView view) {
        this.view = view;

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
        mMasterList = new ArrayList<>();

        // Update UI
        view.onNewBusinessListRequested();
        view.incrementSecondaryProgress_BusinessProgressBar(INITIAL_YELPAPICALL_PROGRESS_VALUE);


        // Call Yelp
        Call<YelpResponse> call = mApiService.getBusinesses(term, location, radius);
        call.enqueue(new Callback<YelpResponse>() {
            @Override
            public void onResponse(Call<YelpResponse> call, Response<YelpResponse> response) {

                final YelpResponse yelpResponse = response.body();

                view.incrementProgress_BusinessProgressBar(INITIAL_YELPAPICALL_PROGRESS_VALUE);

                if (yelpResponse != null) {
                    // Handle Yelp API "error"
                    if (yelpResponse.getError() != null) {
                        Log.e(LOG_TAG, "YELP API ERROR RETURNED: " + yelpResponse.getError().getText()
                                + " ID: " + yelpResponse.getError().getId());
                        view.stopRefreshAnimation();
                        view.showSnackBarIndefinite("Yelp API Error: " + yelpResponse.getError().getText());
                    }

                    // Handle case where there's no error but no results are returned
                    else if (yelpResponse.getBusinesses().size() == 0) {
                        Log.d(LOG_TAG, "Yelp API returned no results");
                        view.stopRefreshAnimation();
                        view.showSnackBarIndefinite("No businesses found.");
                    }

                    // Handle case where there are 20 or less results
                    else if (yelpResponse.getTotal() <= 20) {
                        mMasterList.addAll(yelpResponse.getBusinesses());
                        mTotalResultsAsPerFirstYelpCall = yelpResponse.getTotal();
                        filterListAndUpdateUi();
                    }

                    // Handle more than 20 results
                    else {
                        getMoreThan20Results(yelpResponse, term, location, radius);
                    }
                } else {
                    view.showSnackBarIndefinite("Yelp API Error: Null response.");
                    view.stopRefreshAnimation();
                }
            }

            @Override
            public void onFailure(Call<YelpResponse> call, Throwable t) {
                Log.e(LOG_TAG, "Retrofit FAILURE", t);
                t.printStackTrace();
                view.stopRefreshAnimation();
                view.showSnackBarIndefinite("Yelp API error.  Check your internet connection or perhaps there's a problem with Yelp at the moment.");
            }
        });
    }

    private void getMoreThan20Results(final YelpResponse yelpResponse, String term, String location, String radius) {

        // Reset mCallLog
        mCallLog = new HashMap<>();

        // Using an array `businessArray` so that we can use the indexes to keep the results in order since
        // they will be received asynchronously.  Created according to the size returned by the initial Yelp response
        // TODO There will be a problem in any cases where Yelp returns results in excess of the `total` defined in the first call. Replace with List?
        mTotalResultsAsPerFirstYelpCall = yelpResponse.getTotal();
        final Business[] businessArray = new Business[mTotalResultsAsPerFirstYelpCall];

        // Figure out actual number of results to get
        // TODO This is a mess.  mTotalResultsAsPerFirstYelpCall, mActionableResults, etc.  Too many variables
        if(mTotalResultsAsPerFirstYelpCall > AppSettings.MAX_NO_OF_RESULTS){
            mActionableResults = AppSettings.MAX_NO_OF_RESULTS;
        } else {
            mActionableResults = mTotalResultsAsPerFirstYelpCall;
        }

        // Load the first 20
        for (int i = 0; i<yelpResponse.getBusinesses().size() ; i++){
            businessArray[i] = yelpResponse.getBusinesses().get(i);
        }

        // Load the rest
        final int start = yelpResponse.getBusinesses().size();
        for(int offsetInt = start; offsetInt < mActionableResults ; /* i is updated below */ ){
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
                            addBusinessesAndUpdateUiProgress(offsetPointer, businesses, businessArray);
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
                }

                @Override
                public void onFailure(Call<YelpResponse> call, Throwable t) {
                    t.printStackTrace();
                    tryUpdateMasterListandUpdateUI(businessArray);
                }
            });

            // Update UI
            view.incrementSecondaryProgress_BusinessProgressBar(getProgressValue(RESULTS_PER_PAGE));

            // Update `i`
            // We cannot increment `i` by the actual number of responses received because the response
            // is received asynchronously, i.e. we don't have the data at this time
            offsetInt=offsetInt+RESULTS_PER_PAGE;
        }
    }

    private int getProgressValue(int numberOfResults) {
        float temp = ((float)numberOfResults/ mActionableResults) * 100 ;
        return (int) temp;
    }

    private void addBusinessesAndUpdateUiProgress(int offset, List<Business> businessList, Business[] businessArray){
        // Add new batch of businesses to the master array in the correct order
        for(int i=0; i<businessList.size(); i++){
            businessArray[offset+i] = businessList.get(i);
        }

        view.incrementProgress_BusinessProgressBar(getProgressValue(businessList.size()));

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
        for(Map.Entry<Integer, Boolean> entry : mCallLog.entrySet()){
            if(entry.getValue()==false){
                //Utility.reportExecutionTime(this, "areAllCallsReceived() FALSE",startTime);
                return false;
            }
        }
        return true;
    }

    private void filterListAndUpdateUi() {
        // There might be null values if the Yelp Api returns fewer actual results than specified in the response
        // `total` field.  Therefore we should remove any possible null values before passing to the UI
        mMasterList.removeAll(Collections.singleton(null));
        Log.d(LOG_TAG, "Total results received " + mMasterList.size());

        // Pass list to BusinessListManager to be processed and update UI
        BusinessListAdapter businessListAdapter = view.getSuggestionListAdapter();
        List<Business> filteredBusinesses = view.getBusinessListManager().filter(mMasterList);
        businessListAdapter.setBusinessList(filteredBusinesses);
        businessListAdapter.notifyDataSetChanged();

        // Analytics
        Utility.reportExecutionTime(this, "callYelpApi sequence, time to get "+mMasterList.size()+" businesses", mCallYelpApiStartTime);
        view.logFabricAnswersMetric(AppSettings.FABRIC_METRIC_YELPAPI, mCallYelpApiStartTime);

        // UI
        view.onNewBusinessListReceived();
        view.hideProgressLayout(); // This is the final step of the exectuion sequence so hide progress bar layout
        view.stopRefreshAnimation();
        view.getRecyclerViewLayoutManager().scrollToPosition(0);
    }

}
