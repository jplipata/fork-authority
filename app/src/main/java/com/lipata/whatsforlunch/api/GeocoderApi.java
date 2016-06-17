package com.lipata.whatsforlunch.api;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by jlipata on 6/14/16.
 */
public class GeocoderApi {

    static private final String LOG_TAG = GeocoderApi.class.getSimpleName();

    Context mContext;

    public GeocoderApi(Context context) {
        this.mContext = context;
    }

    public Observable<Address> getAddressObservable(final Location location){

        return Observable.defer(new Func0<Observable<Address>>() {
            @Override
            public Observable<Address> call() {
                try {
                    return Observable.just(fetchAddress(location));
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            }
        });
    }

    Address fetchAddress(Location location) throws IOException {
        Log.d(LOG_TAG, "fetchAddress()");
        Geocoder geocoder = new Geocoder(mContext);
        return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
    }

}
