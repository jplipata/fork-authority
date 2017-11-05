package com.lipata.forkauthority.data;

import android.util.Log;

import com.lipata.forkauthority.Util.Utility;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.data.user.BusinessItemRecord;
import com.lipata.forkauthority.data.user.UserRecords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.lipata.forkauthority.ui.BusinessListAdapter.DONTLIKE;

/**
 * I was concerned that some of the iterative O(n) implementations in this class would result in
 * poor performance, however as of 2016/9/8 execution times for this module are approx 20 ms.  Compared to
 * 1-2 secs for getting the device location plus 3-6 seconds to load Yelp data, this seems insignificant.
 */
public class ListRanker {
    private static String LOG_TAG = ListRanker.class.getSimpleName();

    private UserRecords mUserRecords;

    @Inject
    public ListRanker(final UserRecords userRecords) {
        this.mUserRecords = userRecords;
    }

    /**
     * Takes a list of `Business`s and sorts them according to user preferences stored in `UserRecords`
     * @param businessList_Source List to be sorted.
     * @return Returns sorted list in order of "Preferred", "Neutral", "Too Soon", then "Don't Like"
     */
    public List<Business> filter(List<Business> businessList_Source){
        long startTime = System.nanoTime();

        // 3 categories that each business can be filtered to
        List<Business> preferredList = new ArrayList<>();
        List<Business> tooSoonList = new ArrayList<>();
        List<Business> dontLikeList = new ArrayList<>();

        // Make a copy of the source list
        List<Business> businessList_temp = new ArrayList<>();
        businessList_temp.addAll(businessList_Source);

        // Get user data
        HashMap<String, BusinessItemRecord> userRecordMap = mUserRecords.getMap();

        // Iterate through API results, adjust order according to user records
        for(int i=0; i<businessList_Source.size(); i++){
            Business business = businessList_Source.get(i);
            String businessId = business.getId();

            if(userRecordMap.containsKey(businessId)){

                BusinessItemRecord businessItemRecord = userRecordMap.get(businessId);

                long tooSoonClickDate = businessItemRecord.getTooSoonClickDate();
                long dontLikeClickDate = businessItemRecord.getDontLikeClickDate();
                long dismissedDate = businessItemRecord.getDismissedDate();
                int dismissedCount = businessItemRecord.getDismissedCount();

                // Calculate difference between current time
                long dontLikeDelta = System.currentTimeMillis() - dontLikeClickDate;
                long dontLikeDelta_days = dontLikeDelta / 1000 / 60 / 60 / 24; // Convert to days

                Log.d(LOG_TAG, "Match found! Id = " + businessId + " tooSoonClickDate = "
                        + tooSoonClickDate + " dontLikeClickDate = "+ dontLikeClickDate +
                        " dismissedDate = "+dismissedDate
                        + " dismissedCount = "+dismissedCount);

                // On match found, do:

                // Update the `business` object in memory
                business.setDontLikeClickDate(dontLikeClickDate);
                business.setTooSoonClickDate(tooSoonClickDate);
                business.setDismissedCount(dismissedCount);

                // Handle Like case

                if(dontLikeClickDate==-1){

                    // Assign it to the "Preferred" list, but only if it's not "too soon"

                    if(business.getTooSoonClickDate()==0 || business.isTooSoonClickDateExpired()){

                        preferredList.add(business);
                        businessList_temp.set(i, null); // Remove business from original list
                        Log.v(LOG_TAG, "filter() deemed PREFERRED");
                    } else Log.v(LOG_TAG, "filter() deemed LIKED BUT TOO SOON, not assigned to the PREFERRED LIST");
                }

                // Handle Dont Like case

                if (dontLikeClickDate > 0) {

                    // Add to DontLike list, unless expired
                    if (dontLikeDelta_days < AppSettings.DONTLIKE_THRESHOLD_INDAYS) {
                        // Not expired
                        Log.v(LOG_TAG, "filter() Deemed DON'T LIKE!");
                        dontLikeList.add(business);
                        businessList_temp.set(i, null); // Remove business from original list
                    } else {
                        // Expired
                        Log.v(LOG_TAG, "filter() DontLike EXPIRED, not assigned to DONTLIKE list");

                        // Update SharedPrefs
                        Log.d(LOG_TAG, String.format("filter() DontLike EXPIRED, resetting %s in UserRecords", business.getName()));
                        mUserRecords.updateClickDate(business, 0, DONTLIKE);
                        mUserRecords.commit();

                        // Update in-memory object
                        business.setDontLikeClickDate(0);
                    }
                }

                // Handle the "Too Soon" case:

                if(tooSoonClickDate!=0) {
                    if (!business.isTooSoonClickDateExpired()) {
                        Log.v(LOG_TAG, "filter() Deemed too soon!");
                        tooSoonList.add(business);
                        businessList_temp.set(i, null); // Remove business from original list
                    } else Log.v(LOG_TAG, "filter() TooSoon EXPIRED");
                }
            }
        }

        // Pare down results
        // Let's try only displaying roughly 100 results, I don't think we need more than that
        // Note: This is separate from fetching results from the backend. When fetching results
        // from the backend, you want to fetch a higher number to make sure you don't miss any
        // businesses the user likes
        if(businessList_temp.size() > AppSettings.RESULTS_TO_DISPLAY_MAX){
            Log.d(LOG_TAG, "Paring down businessList_temp. Original size "+businessList_temp.size()+
                    ". "+(businessList_temp.size()- AppSettings.RESULTS_TO_DISPLAY_MAX)+" items removed");
            businessList_temp = businessList_temp.subList(0, AppSettings.RESULTS_TO_DISPLAY_MAX);
        }

        // Remove null elements
        businessList_temp.removeAll(Collections.singleton(null));

        // Combine the lists for display
        List<Business> newList = new ArrayList<>();
        newList.addAll(preferredList);
        newList.addAll(tooSoonList);
        newList.addAll(businessList_temp);
        newList.addAll(dontLikeList);
        Log.d(LOG_TAG, "Final list size "+newList.size());

        // That's it! Return the filtered list.
        Utility.reportExecutionTime(this, "BusinessList filter()",startTime);
        return newList;
    }
}
