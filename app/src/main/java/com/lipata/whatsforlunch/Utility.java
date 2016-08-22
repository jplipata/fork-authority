package com.lipata.whatsforlunch;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by jlipata on 6/21/16.
 */
public class Utility {

    public static String formatDate(long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int month = calendar.get(Calendar.MONTH)+1;
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
     * This is a convenience method you can call at the end of a method to compute execution time.
     * It displays in both nanoseconds and seconds.
     *
     * @param object Use `this` from calling class to generate the typical LOG_TAG
     * @param metricName Your method name for identification
     * @param startTime The start time to compare from
     */
    public static void reportExecutionTime(Object object, String metricName, long startTime) {
        String LOG_TAG = object.getClass().getSimpleName();
        long executionTime = System.nanoTime()-startTime;
        long executionTime_ms = executionTime / 1000000;
        Log.d(LOG_TAG, "Execution time: "+metricName+" = " + executionTime+
                " nanoseconds or " + executionTime_ms + " ms");
    }
}
