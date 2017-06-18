package com.lipata.forkauthority.api.yelp3;

import com.lipata.forkauthority.api.yelp3.entities.SearchResponse;
import com.lipata.forkauthority.api.yelp3.entities.TokenResponse;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Yelp3Api {
    String AUTH_FORMAT = "Bearer %s";
    int SEARCH_LIMIT = 50;

    @FormUrlEncoded
    @POST("oauth2/token")
    Single<TokenResponse> token(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );

    @GET("v3/businesses/search")
    Single<SearchResponse> search(
            @Header("Authorization") String authorization,
            @Query("term") String term,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("radius") int radius,
            // Optional. Number of business results to return. By default, it will return 20. Maximum is 50.
            @Query("limit") int limit
    );

    @GET("v3/businesses/search")
    Single<SearchResponse> search(
            @Header("Authorization") String authorization,
            @Query("term") String term,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("radius") int radius,
            // Optional. Number of business results to return. By default, it will return 20. Maximum is 50.
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    public interface GrantType {
        String CLIENT_CREDENTIALS = "client_credentials";
    }
}
