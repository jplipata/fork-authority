package com.lipata.whatsforlunch.api;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.lipata.whatsforlunch.ui.MainActivity;

import java.io.IOException;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by jlipata on 6/14/16.
 */
public class MyGeocoder {

    static private final String LOG_TAG = MyGeocoder.class.getSimpleName();

    MainActivity mMainActivity;

    public MyGeocoder(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }

    Address getAddress(Location location) throws IOException {
        Log.d(LOG_TAG, "getAddress()");
        Geocoder geocoder = new Geocoder(mMainActivity);
        return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
    }

    public Observable<Address> getAddressObservable(final Location location){

        return Observable.defer(new Func0<Observable<Address>>() {
            @Override
            public Observable<Address> call() {
                try {
                    return Observable.just(getAddress(location));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }
}
