package com.lipata.forkauthority.Util;

import android.location.Address;
import android.util.Log;

import java.util.Calendar;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jlipata on 6/21/16.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String formatDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(month)
                .append("/")
                .append(day)
                .append("/")
                .append(year);

        return stringBuilder.toString();
    }

    /**
     * This is a convenience method you can call to compute execution time.
     * It displays in both nanoseconds and seconds.
     *
     * @param object     Use `this` from calling class to generate the typical LOG_TAG
     * @param metricName Your metric name for identification
     * @param startTime  The start time to compare from
     */
    public static void reportExecutionTime(Object object, String metricName, long startTime) {
        String LOG_TAG = object.getClass().getSimpleName();
        long executionTime = System.nanoTime() - startTime;
        long executionTime_ms = executionTime / 1000000;
        Log.d(LOG_TAG, "Execution time: " + metricName + " = " + executionTime +
                " nanoseconds or " + executionTime_ms + " ms");
    }

    public static <R> SingleSource<R> applySchedulers(Single<R> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     *
     * @param address
     * @return String parsed from Address if all fields are available, otherwise null
     */
    public static String parseLocAddress(Address address) {
        if (address.getSubLocality() != null &&
                address.getAdminArea() != null &&
                address.getPostalCode() != null &&
                !address.getSubLocality().isEmpty() &&
                !address.getAdminArea().isEmpty() &&
                !address.getPostalCode().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb
                    .append(address.getSubLocality())
                    .append(", ")
                    .append(address.getAdminArea())
                    .append(" ")
                    .append(address.getPostalCode());
            return sb.toString();

        } else {
            return null;
        }

    }
}
