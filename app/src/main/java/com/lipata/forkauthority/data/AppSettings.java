package com.lipata.forkauthority.data;

/**
 * Created by jlipata on 2/29/16.
 */
public class AppSettings {

    // Yelp query
    public static String SEARCH_TERM = "food";
    public static int SEARCH_RADIUS = 1000; // Search radius in meters. If the value is too large, a AREA_TOO_LARGE error may be returned. The max value is 40000 meters (25 miles).

    /**
     * Max number of businesses that will be fetched from Yelp.  We were previously fetching all available
     * results, however load times were taking up to 1 minute on devices with slower connections
     *
     * 9/5/2016 Analyzing execution times of the entire fetch businesses sequence, fetching data from Yelp API is
     * the most time consuming part. With `MAX_NO_OF_RESULTS = 240` we are averaging 4-6 seconds execution
     * time over wifi (I assume mobile data would be even slower).
     * `MAX_NO_OF_RESULTS = 120` averages 2-3 seconds
     */
    public static int MAX_NO_OF_RESULTS = 160;

    public static int RESULTS_TO_DISPLAY_MAX = 100;


    // Timings

    public static long TOOSOON_THRESHOLD = 5 * 24 * 60 * 60 * 1000; // 5 days in milliseconds
    public static int DONTLIKE_THRESHOLD_INDAYS = 90; // 90 days

    /* "Age" of location data in milliseconds before it becomes "stale"
     */
    public static double LOCATION_LIFESPAN = 5 * 1000;


    /*
     * The DISMISSED_THRESHOLD constant should be based on how long it typically takes a person to
     * decide what to eat for lunch.
     * The amount of time should last long enough for one user 'session', i.e. as long as it takes to
     * decide what to eat
     */
    public static long DISMISSED_THRESHOLD = 1000 * 60 * 30;

    // Fabric metrics
    public static final String FABRIC_METRIC_GOOGLEPLAYAPI = "GooglePlayApi get location";
    public static final String FABRIC_METRIC_YELPAPI = "YelpApi call";
    public static final String FABRIC_METRIC_FETCH_BIZ_SEQUENCE = "Fetch Businesses";


}
