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

        // Items added to the top of the list must be added at the end of the operation,
        // otherwise indexes get shifted and the filter goes haywire
        TopStack topStack = new TopStack();

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

                    // Calculate difference between current time and tooSoonClickDate
                    long tooSoonDelta = System.currentTimeMillis() - tooSoonClickDate;

                    // Calculate difference between current time
                    long dontLikeDelta = System.currentTimeMillis() - dontLikeClickDate;
                    long dontLikeDelta_days = dontLikeDelta / 1000 / 60 / 60 / 24; // Convert to days

                    long dismissedDelta = System.currentTimeMillis() - dismissedDate;

                    Log.d(LOG_TAG, "Match found! Id = " + businessId + " tooSoonClickDate = "
                            + tooSoonClickDate + " dontLikeClickDate = "+ dontLikeClickDate +
                            " dismissedDate = "+dismissedDate);

                    // On match found, do:

                        // Handle Like case

                            if(dontLikeClickDate==-1){
                                Log.d(LOG_TAG, "filter() deemed LIKE");

                                // Update the `business` object
                                business.setDontLikeClickDate(BusinessItemRecord.LIKE_FLAG);

                                // Move item to top of list, but only if
                                // a) it is not already at the top of the list
                                // b) it is not 'too soon' or c) 'dismissed'
                                if(i!=0 && tooSoonDelta>=AppSettings.TOOSOON_THRESHOLD
                                        && dismissedDelta>=AppSettings.DISMISSED_THRESHOLD) { // Check if item is not already at top of list
                                    topStack.addItemToTopStack(business);
                                    businessList_Filtered.set(i, null);
                                } else {
                                    // Item is already at top of list
                                }
                            }

                        // Handle Dont Like case

                            if (dontLikeClickDate > 0) {
                                // Update the `business` object
                                business.setDontLikeClickDate(dontLikeClickDate);

                                // Move to bottom of list unless expired
                                if (dontLikeDelta_days < AppSettings.DONTLIKE_THRESHOLD_INDAYS) {
                                    Log.d(LOG_TAG, "filter() Deemed DON'T LIKE!");
                                    // Move item down the list
                                    businessList_Filtered = moveItemToBottom(businessList_Filtered, i);
                                } else {
                                    Log.d(LOG_TAG, "filter() DontLike EXPIRED");
                                }

                            }

                        // Handle the "Too Soon" case:

                        if(tooSoonClickDate!=0) {
                            // Update the `business` object to include tooSoonClickDate;
                            business.setTooSoonClickDate(tooSoonClickDate);

                            if (tooSoonDelta < AppSettings.TOOSOON_THRESHOLD) {
                                Log.d(LOG_TAG, "filter() Deemed too soon!");
                                // Move item down the list
                                businessList_Filtered = moveItemToBottom(businessList_Filtered, i);
                            } else {
                                Log.d(LOG_TAG, "filter() TooSoon EXPIRED");
                            }

                        }

                        // Handle Dismissed case
                        // Temporarily ignoring this case
                        // Dismissed items will not be filtered, i.e. if the user refreshes, any dismissed
                        // items will 'come back'
//
//                            if(businessItemRecord.getDismissedDate()!=0){
//                                business.setDismissedDate(dismissedDate);
//                                if (dismissedDelta < AppSettings.DISMISSED_THRESHOLD){
//                                    Log.d(LOG_TAG, "filter() Deemed dismissed!");
//                                    businessList_Filtered = moveItemToBottom(businessList_Filtered, i);
//                                } else {
//                                    Log.d(LOG_TAG, "filter() Dismissed EXPIRED");
//
//                                }
//                            }


                    break;  // Once you've found the match, there's no need to keep going. Exit the `for` loop
                }
            }

        }

        // Remove null elements
        businessList_Filtered.removeAll(Collections.singleton(null));

        // Add items from TopStack, if any
        if (topStack.size()>0) {
            businessList_Filtered.addAll(0, topStack.getTopStack());
        }

        // That's it! Return the filtered list.
        return businessList_Filtered;
    }

    class TopStack {

        List<Business> mTopStack;

        public TopStack() {
            this.mTopStack = new ArrayList<>();
        }

        // Adds items to list of items to be sorted at top of array, to be added at end of filter process
        void addItemToTopStack(Business business){
            mTopStack.add(0, business);
        }

        List<Business> getTopStack(){
            return mTopStack;
        }

        int size(){
            return mTopStack.size();
        }
    }


    // This returns a list with null values, nulls still need to be removed after new list is returned
    public List<Business> moveItemToBottom(List<Business> businessList, int itemPosition){
        Business business = businessList.get(itemPosition);
        businessList.add(business);
        businessList.set(itemPosition, null);
        Log.d(LOG_TAG, "moveItemToBottom() Item " + itemPosition + " moved to bottom");
        return businessList;
    }

    // This returns a list with null values, nulls still need to be removed after new list is returned
    public List<Business> moveItemToTop(List<Business> businessList, int itemPosition){
        if(itemPosition!=0) {
            Business business = businessList.get(itemPosition);
            businessList.add(0, business);//
            businessList.set(itemPosition+1, null); // Needs "+1" because you've added a new item at index 0 so the others have shifted down
            Log.d(LOG_TAG, "moveItemToTop() Item "+ itemPosition + " moved to top");
        } else {
            Log.d(LOG_TAG, "moveItemToTop() Item is already on top");
        }
        return businessList;
    }
}
