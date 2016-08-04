package com.lipata.whatsforlunch.api.yelp;

import android.util.Log;

import com.lipata.whatsforlunch.ApiKeys;
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

    OkHttpOAuthConsumer consumer;
    HttpLoggingInterceptor httpLoggingInterceptor;
    OkHttpClient okHttpClient;
    Retrofit retrofit;
    final Endpoints apiService;

    MainActivity mMainActivity;

    List<Business> mBusinessList = new ArrayList<>(); // This is our main data
    int mTotal;

    public YelpApi(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;

        // OAuth
        consumer = new OkHttpOAuthConsumer(ApiKeys.CONSUMER_KEY, ApiKeys.CONSUMER_SECRET);
        consumer.setTokenWithSecret(ApiKeys.TOKEN, ApiKeys.TOKEN_SECRET);

        // Logger
        httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .addInterceptor(httpLoggingInterceptor) // As per tutorial: We recommend to add logging as the last interceptor, because this will also log the information which you added with previous interceptors to your request.
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(Endpoints.class);

    }

    public void callYelpApi(final String term, final String location, final String radius){


        Call<YelpResponse> call = apiService.getBusinesses(term, location, radius);
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
                    mBusinessList.addAll(yelpResponse.getBusinesses());
                    mTotal = yelpResponse.getTotal();
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

        mTotal = yelpResponse.getTotal();
        final Business[] businessArray = new Business[mTotal];

        // Load the first 20
        for (int i = 0; i<yelpResponse.getBusinesses().size() ; i++){
            businessArray[i] = yelpResponse.getBusinesses().get(i);
        }

        // Load the rest
        final int start = yelpResponse.getBusinesses().size();

        for(int i = start; i<mTotal; ){
            String offset = Integer.toString(i);
            final int offsetPointer = i;

            Call<YelpResponse> call2 = apiService.getMoreBusinesses(term, location, radius, offset);
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

            // must update i
            i=i+20;
        }
    }

    private void addBusinesses(int offset, List<Business> businessList, Business[] businessArray){
        for(int i=0; i<businessList.size(); i++){
            businessArray[offset+i] = businessList.get(i);
        }

        // Check to see if array is full
        if(isArrayFull(businessArray)){
            mBusinessList.clear();
            mBusinessList.addAll(Arrays.asList(businessArray));
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
        List<Business> filteredBusinesses = mMainActivity.getBusinessListManager().filter(mBusinessList);
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
