package com.lipata.whatsforlunch;

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
public class BusinessListFilter {

    public static long TOOSOON_THRESHOLD = 5 * 24 * 60 * 60 * 1000; // 5 days in milliseconds
    public static int DONTLIKE_THRESHOLD = 90; // 90 days

    private static String LOG_TAG = BusinessListFilter.class.getSimpleName();

    List<Business> mBusinessList_Source;
    UserRecords mUserRecords;
    List<Business> mBusinessList_Filtered = new ArrayList<>();
    Context mContext;

    public BusinessListFilter(List<Business> businessList_Source, Context context, UserRecords userRecords) {
        this.mBusinessList_Source = businessList_Source;
        this.mUserRecords = userRecords;
        this.mContext = context;
    }

    // Returns a filtered list
    List<Business> filter(){

        // Get user data
        List<BusinessItemRecord> userRecordList = mUserRecords.getList();

        // Iterate through API results, adjust order according to user records
        mBusinessList_Filtered.addAll(mBusinessList_Source);
        for(int i=0; i<mBusinessList_Source.size(); i++){
            Business business = mBusinessList_Source.get(i);
            String businessId = business.getId();

            // Look for this `businessId` in the user records
            for (int j=0; j<userRecordList.size(); j++){
                BusinessItemRecord businessItemRecord = userRecordList.get(j);
                if(businessItemRecord.getId().equals(businessId)){
                    long tooSoonClickDate = businessItemRecord.getTooSoonClickDate();
                    long dontLikeClickDate = businessItemRecord.getDontLikeClickDate();
                    int rating = businessItemRecord.getRating();
                    Log.d(LOG_TAG, "Match found. Id = " + businessId + " tooSoonClickDate = "
                            + tooSoonClickDate + " dontLikeClickDate = "+ dontLikeClickDate +
                            " rating = " + rating);

                    // On match found, do:

                        // Handle the "Too Soon" case:

                            if(businessItemRecord.getTooSoonClickDate()!=0) {
                                // Update the `business` object to include tooSoonClickDate;
                                business.setTooSoonClickDate(tooSoonClickDate);

                                // Calculate difference between current time and tooSoonClickDate
                                long tooSoonDelta = System.currentTimeMillis() - tooSoonClickDate;

                                if (tooSoonDelta < TOOSOON_THRESHOLD) {
                                    // Move item down the list
                                    mBusinessList_Filtered = moveItemToBottom(mBusinessList_Filtered, i);
                                }

                            }

                        // Handle Dont Like case

                            if(businessItemRecord.getDontLikeClickDate()!=0) {
                                // Update the `business` object
                                business.setDontLikeClickDate(dontLikeClickDate);

                                // Calculate difference between current time
                                long dontLikeDelta = System.currentTimeMillis() - dontLikeClickDate;
                                long dontLikeDelta_days = dontLikeDelta / 1000 / 60 / 60 / 24; // Convert to days

                                if (dontLikeDelta_days < DONTLIKE_THRESHOLD) {
                                    // Move item down the list
                                    mBusinessList_Filtered = moveItemToBottom(mBusinessList_Filtered, i);
                                }

                            }


                    break;  // Once you've found the match, there's no need to keep going. Exit the `for` loop
                }
            }

        }

        // Remove null elements
        mBusinessList_Filtered.removeAll(Collections.singleton(null));

        // That's it! Return the filtered list.
        return mBusinessList_Filtered;
    }


    // This returns a list with null values, nulls still need to be removed after new list is returned
    List<Business> moveItemToBottom(List<Business> businessList, int itemPosition){
        Business business = businessList.get(itemPosition);
        businessList.add(business);
        businessList.set(itemPosition, null);
        return businessList;
    }
}
