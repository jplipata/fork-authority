package com.lipata.forkauthority.api.yelp3;

import android.util.Log;

import com.google.gson.Gson;
import com.lipata.forkauthority.BuildConfig;
import com.lipata.forkauthority.api.yelp3.entities.TokenResponse;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.lipata.forkauthority.api.yelp3.Yelp3Api.AuthParams.CLIENT_ID;
import static com.lipata.forkauthority.api.yelp3.Yelp3Api.AuthParams.CLIENT_SECRET;
import static com.lipata.forkauthority.api.yelp3.Yelp3Api.AuthParams.GRANT_TYPE;

/**
 * Created by jlipata on 11/24/17.
 */

public class Yelp3ApiAuthenticator implements Authenticator {
    private final String LOG_TAG = Yelp3ApiAuthenticator.class.getSimpleName();

    private final OkHttpClient client;
    private TokenManager tokenManager;

    public Yelp3ApiAuthenticator(final TokenManager tokenManager) {
        this.tokenManager = tokenManager;

        final OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(httpLoggingInterceptor);
        }
        this.client = clientBuilder.build();
    }


    @Nullable
    @Override
    public synchronized Request authenticate(final Route route, final Response response)
            throws IOException {
        Log.e(LOG_TAG, "Status 401");

        TokenResponse tokenResponse = requestToken(response);

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            // Did not receive a valid response, do not retry
            return null;
        } else {
            tokenManager.setSharedPrefToken(tokenResponse.getAccessToken());
        }

        String token = tokenManager.getToken();

        Request modifiedResponse = response
                .request()
                .newBuilder()
                .header("Authorization", token)
                .build();

        return modifiedResponse;
    }

    private TokenResponse requestToken(Response response) throws IOException {
        final FormBody requestBody = new FormBody.Builder()
                .addEncoded(CLIENT_ID, BuildConfig.YELPFUSION_CLIENT_ID)
                .addEncoded(CLIENT_SECRET, BuildConfig.YELPFUSION_CLIENT_SECRET)
                .addEncoded(GRANT_TYPE, Yelp3Api.GrantTypes.CLIENT_CREDENTIALS)
                .build();

        final Request request = new Request.Builder()
                .post(requestBody)
                .url("https://api.yelp.com/oauth2/token")
                .build();

        TokenResponse tokenResponse = new Gson().fromJson(
                client.newCall(request).execute().body().string(),
                TokenResponse.class);

        return tokenResponse;
    }
}