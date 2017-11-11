package com.lipata.forkauthority.api.yelp3;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.lipata.forkauthority.BuildConfig;
import com.lipata.forkauthority.R;
import com.lipata.forkauthority.api.yelp3.entities.TokenResponse;
import com.lipata.forkauthority.util.Utility;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

public class TokenManager {
    private static final String LOG_TAG = "TokenManager";
    private static final String AUTH_FORMAT = "Bearer %s";

    private final SharedPreferences sharedPrefs;
    private final String tokenKey;
    private final Yelp3ApiClient api;

    @Inject
    public TokenManager(final Context context, final Yelp3ApiClient api) {
        this.api = api;
        Resources resources = context.getResources();
        sharedPrefs = context.getSharedPreferences(resources.getString(R.string.shared_prefs_file), Context.MODE_PRIVATE);
        tokenKey = resources.getString(R.string.key_yelp3_token);
    }

    /**
     * Returns either the cached token from SharedPrefs, formatted, or else fetches a new one.
     * If the existing token turns out to be invalid, you should clear sharedPrefs so that a new
     * token will be fetched
     */
    public synchronized String getToken() {
        long startTime = System.nanoTime();
        final String cachedToken = sharedPrefs.getString(tokenKey, "null");
        Log.d(LOG_TAG, String.format("SharedPrefs YelpV3Token %s", cachedToken));
        if (cachedToken.equals("null")) {
            String tokenString = api
                    .token(
                            Yelp3Api.GrantType.CLIENT_CREDENTIALS,
                            BuildConfig.YELPFUSION_CLIENT_ID,
                            BuildConfig.YELPFUSION_CLIENT_SECRET)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(this::setSharedPrefToken)
                    .map(tokenResponse -> String.format(AUTH_FORMAT, tokenResponse.getAccessToken()))
                    .blockingGet();
            Utility.reportExecutionTime(this, "getToken()", startTime);
            return tokenString;
        } else {
            Log.d(LOG_TAG, "YelpV3Token found in SharedPrefs");
            return String.format(AUTH_FORMAT, cachedToken);
        }
    }

    private void setSharedPrefToken(TokenResponse tokenResponse) {
        sharedPrefs
                .edit()
                .putString(tokenKey, tokenResponse.getAccessToken())
                .apply();
    }
}
