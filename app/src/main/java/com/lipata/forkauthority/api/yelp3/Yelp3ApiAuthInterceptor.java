package com.lipata.forkauthority.api.yelp3;

import com.lipata.forkauthority.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jlipata on 11/24/17.
 */

public class Yelp3ApiAuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain
                .request()
                .newBuilder()
                .addHeader("Authorization", "Bearer "+ BuildConfig.YELPFUSION_API_KEY)
                .build();

        return chain.proceed(request);
    }
}
