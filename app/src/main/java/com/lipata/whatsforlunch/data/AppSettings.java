package com.lipata.whatsforlunch.data;

/**
 * Created by jlipata on 2/29/16.
 */
public class AppSettings {
    // Yelp query
    public static String SEARCH_TERM = "lunch";
    public static int SEARCH_RADIUS = 1000; // Search radius in meters. If the value is too large, a AREA_TOO_LARGE error may be returned. The max value is 40000 meters (25 miles).

    // Timings
    public static double LOCATION_LIFESPAN = 4 * 1000; // "Age" of location data in milliseconds before it becomes "stale"
    public static long TOOSOON_THRESHOLD = 5 * 24 * 60 * 60 * 1000; // 5 days in milliseconds
    public static int DONTLIKE_THRESHOLD_INDAYS = 90; // 90 days

    /*
     * The DISMISSED_THRESHOLD constant should be based on how long it typically takes a person to
     * decide what to eat for lunch.
     * The amount of time should last long enough for one user 'session', i.e. as long as it takes to
     * decide what to eat
     */
    public static long DISMISSED_THRESHOLD = 1000 * 60 * 30;
}
