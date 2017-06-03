package com.lipata.forkauthority.api;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;

import io.reactivex.Single;

/**
 * Created by jlipata on 6/14/16.
 */
public class GeocoderApi {

    static private final String LOG_TAG = GeocoderApi.class.getSimpleName();

    private Context mContext;

    public GeocoderApi(Context context) {
        this.mContext = context;
    }

    Single<Address> getAddressObservable(final Location location) {
        try {
            return Single.just(fetchAddress(location));
        } catch (IOException e) {
            e.printStackTrace();
            return Single.error(e);
        }
    }


    private Address fetchAddress(Location location) throws IOException {
        Log.d(LOG_TAG, "fetchAddress()");
        Geocoder geocoder = new Geocoder(mContext); //TODO DI this guy
        return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
    }

}
