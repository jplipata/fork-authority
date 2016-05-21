package com.lipata.whatsforlunch.api.yelp_api;


import com.lipata.whatsforlunch.api.yelp_api.model.YelpResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jlipata on 5/15/16.
 */
public interface EndpointInterface {

    @GET("v2/search")
    Call<YelpResponse> getBusinesses(@Query("term") String term, @Query("ll") String location, @Query("radius_filter") String radius);

}
