package com.lipata.forkauthority.api.yelp3;

import com.lipata.forkauthority.api.yelp3.entities.SearchResponse;
import com.lipata.forkauthority.api.yelp3.entities.TokenResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class Yelp3ApiClient implements Yelp3Api {

    private static final String BASE_URL = "https://api.yelp.com/";

    private final Yelp3Api api;

    @Inject
    public Yelp3ApiClient(
            final Yelp3ApiAuthInterceptor authInterceptor,
            final Yelp3ApiAuthenticator authenticator) {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(authenticator)
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        api = retrofit.create(Yelp3Api.class);
    }

    @Override
    public Single<TokenResponse> token(
            final String grantType,
            final String clientId,
            final String clientSecret) {
        return api.token(grantType, clientId, clientSecret);
    }

    @Override
    public Single<SearchResponse> search(
            String term,
            String latitude,
            String longitude,
            int radius,
            int limit) {
        return api.search(term, latitude, longitude, radius, limit);
    }

    @Override
    public Single<SearchResponse> search(
            String term,
            String latitude,
            String longitude,
            int radius,
            int limit,
            int offset) {
        return api.search(term, latitude, longitude, radius, limit, offset);
    }
}
