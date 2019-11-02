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
    int SEARCH_LIMIT = 50;

    @GET("v3/businesses/search")
    Single<SearchResponse> search(
            @Query("term") String term,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("radius") int radius,
            // Optional. Number of business results to return. By default, it will return 20. Maximum is 50.
            @Query("limit") int limit
    );

    @GET("v3/businesses/search")
    Single<SearchResponse> search(
            @Query("term") String term,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("radius") int radius,
            // Optional. Number of business results to return. By default, it will return 20. Maximum is 50.
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
