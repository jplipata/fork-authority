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
                    int rating = businessItemRecord.getRating();
                    Log.d(LOG_TAG, "Match found. Id = " + businessId + " tooSoonClickDate = " + tooSoonClickDate + " rating = " + rating);

                    // On match found, do:

                        // Handle the "Too Soon" case:

                            // Update the `business` object to include tooSoonClickDate;
                            business.setTooSoonClickDate(tooSoonClickDate);

                            // Calculate difference between current time and tooSoonClickDate
                            long tooSoonDelta = System.currentTimeMillis()-tooSoonClickDate;

                            if (tooSoonDelta < TOOSOON_THRESHOLD){
                                // Move item down the list
                                mBusinessList_Filtered = moveItemTooSoon(mBusinessList_Filtered, i, business);
                            }


                        // Handle `rating` case.  If rating is high, move item up.  If rating is low, move item down.


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
    List<Business> moveItemTooSoon (List<Business> businessList, int itemPosition, Business business){
        businessList.add(business);
        businessList.set(itemPosition, null);
        return businessList;
    }
}
