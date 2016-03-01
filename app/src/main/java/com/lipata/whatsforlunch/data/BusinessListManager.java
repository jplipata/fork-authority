package com.lipata.whatsforlunch.data;

import android.content.Context;
import android.util.Log;

import com.lipata.whatsforlunch.data.user.BusinessItemRecord;
import com.lipata.whatsforlunch.data.user.UserRecords;
import com.lipata.whatsforlunch.data.yelppojo.Business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jlipatap on 1/17/16.
 */
public class BusinessListManager {
    private static String LOG_TAG = BusinessListManager.class.getSimpleName();

    UserRecords mUserRecords;
    Context mContext;

    public BusinessListManager(Context context, UserRecords userRecords) {
        this.mUserRecords = userRecords;
        this.mContext = context;
    }

    // Returns a filtered list
    public List<Business> filter(List<Business> businessList_Source){

        List<Business> businessList_Filtered = new ArrayList<>();

        // Get user data
        List<BusinessItemRecord> userRecordList = mUserRecords.getList();

        // Iterate through API results, adjust order according to user records
        businessList_Filtered.addAll(businessList_Source);
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
                    Log.d(LOG_TAG, "Match found. Id = " + businessId + " tooSoonClickDate = "
                            + tooSoonClickDate + " dontLikeClickDate = "+ dontLikeClickDate +
                            " dismissedDate = "+dismissedDate);

                    // On match found, do:

                        // Handle the "Too Soon" case:

                            if(businessItemRecord.getTooSoonClickDate()!=0) {
                                // Update the `business` object to include tooSoonClickDate;
                                business.setTooSoonClickDate(tooSoonClickDate);

                                // Calculate difference between current time and tooSoonClickDate
                                long tooSoonDelta = System.currentTimeMillis() - tooSoonClickDate;

                                if (tooSoonDelta < AppSettings.TOOSOON_THRESHOLD) {
                                    Log.d(LOG_TAG, "filter() Deemed too soon!");
                                    // Move item down the list
                                    businessList_Filtered = moveItemToBottom(businessList_Filtered, i);
                                } else {
                                    Log.d(LOG_TAG, "filter() TooSoon EXPIRED");
                                }

                            }

                        // Handle Dont Like case

                            if(businessItemRecord.getDontLikeClickDate()!=0) {
                                // Update the `business` object
                                business.setDontLikeClickDate(dontLikeClickDate);

                                // Calculate difference between current time
                                long dontLikeDelta = System.currentTimeMillis() - dontLikeClickDate;
                                long dontLikeDelta_days = dontLikeDelta / 1000 / 60 / 60 / 24; // Convert to days

                                if (dontLikeDelta_days < AppSettings.DONTLIKE_THRESHOLD_INDAYS) {
                                    Log.d(LOG_TAG, "filter() Deemed DON'T LIKE!");
                                    // Move item down the list
                                    businessList_Filtered = moveItemToBottom(businessList_Filtered, i);
                                } else {
                                    Log.d(LOG_TAG, "filter() DontLike EXPIRED");

                                }

                            }
                        // Handle Dismissed case

                            if(businessItemRecord.getDismissedDate()!=0){
                                business.setDismissedDate(dismissedDate);
                                long dismissedDelta = System.currentTimeMillis() - dismissedDate;
                                if (dismissedDelta < AppSettings.DISMISSED_THRESHOLD){
                                    Log.d(LOG_TAG, "filter() Deemed dismissed!");
                                    businessList_Filtered = moveItemToBottom(businessList_Filtered, i);
                                } else {
                                    Log.d(LOG_TAG, "filter() Dismissed EXPIRED");

                                }
                            }


                    break;  // Once you've found the match, there's no need to keep going. Exit the `for` loop
                }
            }

        }

        // Remove null elements
        businessList_Filtered.removeAll(Collections.singleton(null));

        // That's it! Return the filtered list.
        return businessList_Filtered;
    }


    // This returns a list with null values, nulls still need to be removed after new list is returned
    public List<Business> moveItemToBottom(List<Business> businessList, int itemPosition){
        Business business = businessList.get(itemPosition);
        businessList.add(business);
        businessList.set(itemPosition, null);
        return businessList;
    }
}
