package com.lipata.whatsforlunch.api.yelp;

import android.util.Log;

import com.lipata.whatsforlunch.ApiKeys;
import com.lipata.whatsforlunch.api.yelp.model.Business;
import com.lipata.whatsforlunch.api.yelp.model.YelpResponse;
import com.lipata.whatsforlunch.ui.BusinessListAdapter;
import com.lipata.whatsforlunch.ui.MainActivity;

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

    MainActivity mMainActivity;

    public YelpApi(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
    }

    public void callYelpApi(String term, String location, String radius){

        // OAuth
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(ApiKeys.CONSUMER_KEY, ApiKeys.CONSUMER_SECRET);
        consumer.setTokenWithSecret(ApiKeys.TOKEN, ApiKeys.TOKEN_SECRET);

        // Logger
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .addInterceptor(httpLoggingInterceptor) // As per tutorial: We recommend to add logging as the last interceptor, because this will also log the information which you added with previous interceptors to your request.
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Endpoints apiService = retrofit.create(Endpoints.class);

        Call<YelpResponse> call = apiService.getBusinesses(term, location, radius);
        call.enqueue(new Callback<YelpResponse>() {
            @Override
            public void onResponse(Call<YelpResponse> call, Response<YelpResponse> response) {

                YelpResponse yelpResponse = response.body();
                List<Business> businessList = yelpResponse.getBusinesses();

                // Handle Yelp API "error"
                if(yelpResponse.getError()!=null){
                    Log.e(LOG_TAG, "YELP API ERROR RETURNED: " + yelpResponse.getError().getText()
                            + " ID: " + yelpResponse.getError().getId());
                    mMainActivity.showSnackBarIndefinite("Yelp API Error: " + yelpResponse.getError().getText());

                }

                // Handle case where there's no error but no results are returned
                // I have not encountered this case, but imagine that it could happen
                else if (businessList.size()==0) {
                    Log.d(LOG_TAG, "Yelp API returned no results");
                    mMainActivity.showSnackBarIndefinite("No businesses found.");
                }

                // Handle expected results
                else {
                    // Manipulate `businessList` to apply customization

                    BusinessListAdapter businessListAdapter = mMainActivity.getSuggestionListAdapter();

                    List<Business> filteredBusinesses = mMainActivity.getBusinessListManager().filter(businessList);
                    businessListAdapter.setBusinessList(filteredBusinesses);
                    businessListAdapter.notifyDataSetChanged();
                }

                // UI stuff
                mMainActivity.stopRefreshAnimation();
                mMainActivity.getRecyclerViewLayoutManager().scrollToPosition(0);

            }

            @Override
            public void onFailure(Call<YelpResponse> call, Throwable t) {
                Log.e(LOG_TAG, "Retrofit FAILURE", t);
                t.printStackTrace();
                //mMainFragment.setYelpText("Yelp API FAILURE");

                mMainActivity.showSnackBarIndefinite("ERROR: Yelp API Failure");

            }
        });

    }
}
