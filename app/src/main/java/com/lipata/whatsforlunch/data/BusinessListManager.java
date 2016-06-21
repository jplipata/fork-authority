package com.lipata.whatsforlunch.data;

import android.content.Context;
import android.util.Log;

import com.lipata.whatsforlunch.api.yelp.model.Business;
import com.lipata.whatsforlunch.data.user.BusinessItemRecord;
import com.lipata.whatsforlunch.data.user.UserRecords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jlipatap on 1/17/16.
 *
 */
public class BusinessListManager {
    private static String LOG_TAG = BusinessListManager.class.getSimpleName();

    UserRecords mUserRecords;
    Context mContext;

    public BusinessListManager(Context context, UserRecords userRecords) {
        this.mUserRecords = userRecords;
        this.mContext = context;
    }

    /**
     * Takes a list of `Business`s and sorts them according to user preferences stored in `UserRecords`
     * @param businessList_Source List to be sorted.
     * @return Returns sorted list.
     */
    public List<Business> filter(List<Business> businessList_Source){

        // 3 categories that each business can be filtered to
        List<Business> preferredList = new ArrayList<>();
        List<Business> tooSoonList = new ArrayList<>();
        List<Business> dontLikeList = new ArrayList<>();

        // Make a copy of the source list
        List<Business> businessList_temp = new ArrayList<>();
        businessList_temp.addAll(businessList_Source);

        // Get user data
        List<BusinessItemRecord> userRecordList = mUserRecords.getList();

        // Iterate through API results, adjust order according to user records  TODO: Replace iteration with HashMap
        for(int i=0; i<businessList_Source.size(); i++){
            Business business = businessList_Source.get(i);
            String businessId = business.getId();

            // Look for this `businessId` in the user records
            for (int j=0; j<userRecordList.size(); j++){
                BusinessItemRecord businessItemRecord = userRecordList.get(j);
                if(businessItemRecord.getId().equals(businessId)){
                    long tooSoonClickDate = businessItemRecord.getTooSoonClickDate();
                    long dontLikeClickDate = businessItemRecord.getDontLikeClickDate();
                    long dismissedDate = businessItemRecord.getDismissedDate();

                    // Calculate difference between current time
                    // TODO If we're going to keep this functionality, this logic should move to the `Business` class
                    long dontLikeDelta = System.currentTimeMillis() - dontLikeClickDate;
                    long dontLikeDelta_days = dontLikeDelta / 1000 / 60 / 60 / 24; // Convert to days

                    Log.d(LOG_TAG, "Match found! Id = " + businessId + " tooSoonClickDate = "
                            + tooSoonClickDate + " dontLikeClickDate = "+ dontLikeClickDate +
                            " dismissedDate = "+dismissedDate);

                    // On match found, do:

                        // Update the `business` object in memory
                        business.setDontLikeClickDate(dontLikeClickDate);
                        business.setTooSoonClickDate(tooSoonClickDate);

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
                                    Log.v(LOG_TAG, "filter() Deemed DON'T LIKE!");
                                    dontLikeList.add(business);
                                    businessList_temp.set(i, null); // Remove business from original list
                                } else Log.v(LOG_TAG, "filter() DontLike EXPIRED, not assigned to DONTLIKE list");
                            }

                        // Handle the "Too Soon" case:

                            if(tooSoonClickDate!=0) {
                                if (!business.isTooSoonClickDateExpired()) {
                                    Log.v(LOG_TAG, "filter() Deemed too soon!");
                                    tooSoonList.add(business);
                                    businessList_temp.set(i, null); // Remove business from original list
                                } else Log.v(LOG_TAG, "filter() TooSoon EXPIRED");
                            }
                        break;  // Once you've found the match, there's no need to keep going. Exit the `for` loop
                }
            }
        }

        // Remove null elements
        businessList_temp.removeAll(Collections.singleton(null));

        // Combine the lists for display (for now).  TODO Implement a better way of displaying the categories
        List<Business> newList = new ArrayList<>();
        newList.addAll(preferredList);
        newList.addAll(businessList_temp);
        newList.addAll(tooSoonList);
        newList.addAll(dontLikeList);

        // That's it! Return the filtered list.
        return newList;
    }
}
