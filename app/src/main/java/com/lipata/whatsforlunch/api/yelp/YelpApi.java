package com.lipata.whatsforlunch.api.yelp;

import android.util.Log;

import com.lipata.whatsforlunch.BuildConfig;
import com.lipata.whatsforlunch.api.yelp.model.Business;
import com.lipata.whatsforlunch.api.yelp.model.YelpResponse;
import com.lipata.whatsforlunch.ui.BusinessListAdapter;
import com.lipata.whatsforlunch.ui.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static final String LOG_TAG = "YelpApi-Retrofit";
    public static final String BASE_URL = "https://api.yelp.com/";
    public static final int RESULTS_PER_PAGE = 20; // However many results the Yelp API returns per page

    OkHttpOAuthConsumer mConsumer;
    HttpLoggingInterceptor mHttpLoggingInterceptor;
    OkHttpClient mOkHttpClient;
    Retrofit mRetrofit;
    final Endpoints mApiService;

    MainActivity mMainActivity;

    List<Business> mMasterList = new ArrayList<>(); // This is our main data
    int mTotalNumberOfResults;

    public YelpApi(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;

        // OAuth
        mConsumer = new OkHttpOAuthConsumer(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);
        mConsumer.setTokenWithSecret(BuildConfig.TOKEN, BuildConfig.TOKEN_SECRET);

        // Logger
        mHttpLoggingInterceptor = new HttpLoggingInterceptor();
        mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

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

        Call<YelpResponse> call = mApiService.getBusinesses(term, location, radius);
        call.enqueue(new Callback<YelpResponse>() {
            @Override
            public void onResponse(Call<YelpResponse> call, Response<YelpResponse> response) {

                final YelpResponse yelpResponse = response.body();

                // Handle Yelp API "error"
                if(yelpResponse.getError()!=null){
                    Log.e(LOG_TAG, "YELP API ERROR RETURNED: " + yelpResponse.getError().getText()
                            + " ID: " + yelpResponse.getError().getId());
                    mMainActivity.stopRefreshAnimation();
                    mMainActivity.showSnackBarIndefinite("Yelp API Error: " + yelpResponse.getError().getText());
                }

                // Handle case where there's no error but no results are returned
                else if (yelpResponse.getBusinesses().size()==0) {
                    Log.d(LOG_TAG, "Yelp API returned no results");
                    mMainActivity.stopRefreshAnimation();
                    mMainActivity.showSnackBarIndefinite("No businesses found.");
                }

                // Handle case where there are 20 or less results
                else if (yelpResponse.getTotal()<=20){
                    mMasterList.addAll(yelpResponse.getBusinesses());
                    mTotalNumberOfResults = yelpResponse.getTotal();
                    filterListAndUpdateUi();
                }

                // Handle more than 20 results
                else {
                    getMoreThan20Results(yelpResponse, term, location, radius);
                }
            }

            @Override
            public void onFailure(Call<YelpResponse> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    private void getMoreThan20Results(final YelpResponse yelpResponse, String term, String location, String radius) {

        // Using an array so that we can use the indexes to keep the results in order since
        // they will be received asynchronously

        mTotalNumberOfResults = yelpResponse.getTotal();
        mMainActivity.showToast(String.format("Retrieving %d businesses...", mTotalNumberOfResults));
        final Business[] businessArray = new Business[mTotalNumberOfResults];

        // Load the first 20
        for (int i = 0; i<yelpResponse.getBusinesses().size() ; i++){
            businessArray[i] = yelpResponse.getBusinesses().get(i);
        }

        // Load the rest
        final int start = yelpResponse.getBusinesses().size();

        for(int i = start; i< mTotalNumberOfResults; /* i is updated below */ ){
            String offset = Integer.toString(i);
            final int offsetPointer = i; // Need a `final` variable to use in the anonymous class below, otherwise I would just use `i`

            Call<YelpResponse> call2 = mApiService.getBusinesses(term, location, radius, offset);
            call2.enqueue(new Callback<YelpResponse>() {
                @Override
                public void onResponse(Call<YelpResponse> call, Response<YelpResponse> response) {
                    YelpResponse yelpResponse2 = response.body();
                    addBusinesses(offsetPointer, yelpResponse2.getBusinesses(), businessArray);
                }

                @Override
                public void onFailure(Call<YelpResponse> call, Throwable t) {
                    handleFailure(t);
                }
            });

            // Update `i`
            i=i+RESULTS_PER_PAGE;
        }
    }

    private void addBusinesses(int offset, List<Business> businessList, Business[] businessArray){
        // Add new batch of businesses to the master array in the correct order
        for(int i=0; i<businessList.size(); i++){
            businessArray[offset+i] = businessList.get(i);
        }

        // Check to see if array is full.  If yes, proceed to filter list and update UI
        // DANGER! If one of the calls never receives a response, this method might not ever get called.
        if(isArrayFull(businessArray)){
            mMasterList.clear();
            mMasterList.addAll(Arrays.asList(businessArray));
            filterListAndUpdateUi();
        }
    }

    private boolean isArrayFull(Business[] businessArray){
        for(int i=0; i<businessArray.length; i++){
            if(businessArray[i]==null) {
                return false;
            }
        }
        return true;
    }

    private void filterListAndUpdateUi() {
        BusinessListAdapter businessListAdapter = mMainActivity.getSuggestionListAdapter();
        List<Business> filteredBusinesses = mMainActivity.getBusinessListManager().filter(mMasterList);
        businessListAdapter.setBusinessList(filteredBusinesses);
        businessListAdapter.notifyDataSetChanged();
        mMainActivity.stopRefreshAnimation();
        mMainActivity.getRecyclerViewLayoutManager().scrollToPosition(0);
    }

    private void handleFailure(Throwable t) {
        Log.e(LOG_TAG, "Retrofit FAILURE", t);
        t.printStackTrace();
        mMainActivity.stopRefreshAnimation();
        mMainActivity.showSnackBarIndefinite("Yelp API error.  Check your internet connection or perhaps there's a problem with Yelp at the moment.");
    }

}
