package com.lipata.whatsforlunch.data;

/**
 * Created by jlipata on 2/29/16.
 */
public class AppSettings {
    public static final String SEARCH_TERM = "restaurants"; // This should not be user-definable at this time
    public static final double LOCATION_LIFESPAN = 10 * 1000; // "Age" of location data in milliseconds before it becomes "stale"
    public static final int SEARCH_RADIUS = 1000; // Search radius in meters. If the value is too large, a AREA_TOO_LARGE error may be returned. The max value is 40000 meters (25 miles).
}
