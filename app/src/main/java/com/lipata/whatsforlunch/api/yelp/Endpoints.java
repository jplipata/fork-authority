package com.lipata.whatsforlunch.api.yelp;


import com.lipata.whatsforlunch.api.yelp.model.YelpResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jlipata on 5/15/16.
 */
public interface Endpoints {

    @GET("v2/search")
    Call<YelpResponse> getBusinesses(@Query("term") String term, @Query("ll") String location,
                                     @Query("radius_filter") String radius);

    @GET("v2/search")
    Call<YelpResponse> getBusinesses(@Query("term") String term, @Query("ll") String location,
                                     @Query("radius_filter") String radius, @Query("offset") String offset);


}
