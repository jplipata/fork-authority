package com.lipata.forkauthority.api.yelp3;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.lipata.forkauthority.R;

import timber.log.Timber;

public class TokenManager {
    private static final String AUTH_FORMAT = "Bearer %s";

    private final SharedPreferences sharedPrefs;
    private final String tokenKey;

    public TokenManager(final Context context, final SharedPreferences sharedPrefs) {
        Resources resources = context.getResources();
        this.tokenKey = resources.getString(R.string.key_yelp3_token);
        this.sharedPrefs = sharedPrefs;
    }

    /**
     * @return Returns formatted token
     */
    public synchronized String getToken() {
        final String cachedToken = sharedPrefs.getString(tokenKey, "null");
        Timber.d(String.format("SharedPrefs YelpV3Token %s", cachedToken));
        return String.format(AUTH_FORMAT, cachedToken);
    }


    public void setSharedPrefToken(final String accessToken) {
        sharedPrefs
                .edit()
                .putString(tokenKey, accessToken)
                .apply();
    }
}
