package com.lipata.forkauthority.api;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.lipata.forkauthority.di.PerApp;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Single;

@PerApp
public class GeocoderApi {

    static private final String LOG_TAG = GeocoderApi.class.getSimpleName();

    private final Geocoder geocoder;

    @Inject
    GeocoderApi(final Context context) {
        geocoder = new Geocoder(context);
    }

    public Single<Address> getAddressObservable(final Location location) {
        try {
            return Single.just(fetchAddress(location));
        } catch (IOException e) {
            e.printStackTrace();
            return Single.error(e);
        }
    }


    private Address fetchAddress(final Location location) throws IOException {
        Log.d(LOG_TAG, "fetchAddress()");
        return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
    }

}
