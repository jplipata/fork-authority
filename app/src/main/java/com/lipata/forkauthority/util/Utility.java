package com.lipata.forkauthority.util;

import java.util.Calendar;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class Utility {

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
        Timber.tag(LOG_TAG).d("Execution time: " + metricName + " = " + executionTime +
                " nanoseconds or " + executionTime_ms + " ms");
    }

    public static <R> SingleSource<R> applySchedulers(Single<R> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
