package com.lipata.whatsforlunch.data.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lipata.whatsforlunch.ui.BusinessListAdapter;
import com.lipata.whatsforlunch.R;
import com.lipata.whatsforlunch.data.yelppojo.Business;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlipata on 2/22/16.
 */
public class UserRecords {
    private static String LOG_TAG = UserRecords.class.getSimpleName();
    List<BusinessItemRecord> mList;
    Context mContext;

    public UserRecords(Context context) {
        this.mContext=context;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.SharedPrefsFile),
                Context.MODE_PRIVATE);

        // If there's an existing list of records, load it
        if (sharedPreferences.contains(mContext.getString(R.string.UserRecordList))){
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<BusinessItemRecord>>(){}.getType();
            ArrayList<BusinessItemRecord> userRecordList = gson.fromJson(sharedPreferences
                    .getString(mContext.getString(R.string.UserRecordList), null), collectionType );
            mList=userRecordList;
        }
        // If there's no existing UserRecords
        else {
            mList = new ArrayList<BusinessItemRecord>();
        }
    }

    public List<BusinessItemRecord> getList(){
        return mList;
    }

    public void addRecord(BusinessItemRecord businessItemRecord){
        mList.add(businessItemRecord);
        Log.d(LOG_TAG, "BusinessItemRecord added");
    }


    public void updateClickDate(Business business, long time, int buttonId){
        Log.d(LOG_TAG, "updateClickDate()");

        // Check for item
        int itemIndex = getItemIndex(business.getId());
        Log.d(LOG_TAG, "getItemIndex() "+itemIndex);

        // -1 means item does not exist
        if(itemIndex==-1){
            Log.d(LOG_TAG, "Item does not exist");
            // if the item doesn't exist:
            BusinessItemRecord businessItemRecord = new BusinessItemRecord();
            businessItemRecord.setId(business.getId());

            // Assign based on button type
            switch(buttonId){
                case BusinessListAdapter.TOOSOON:
                    businessItemRecord.setTooSoonClickDate(time);
                    break;
                case BusinessListAdapter.DONTLIKE:
                    businessItemRecord.setDontLikeClickDate(time);
                    break;
                case BusinessListAdapter.DISMISS:
                    businessItemRecord.setDismissedDate(time);
                    break;
                case BusinessListAdapter.LIKE:
                    businessItemRecord.setDontLikeClickDate(time); // Use "-1" for "Like"
            }

            // Check
            Log.d(LOG_TAG, "businessItemRecord.  Id = " + businessItemRecord.getId() +
                    " tooSoonClickDate = " + businessItemRecord.getTooSoonClickDate()
                    + " dontlikeClickDate = " + businessItemRecord.getDontLikeClickDate()
                    + " dismissedDate = " + businessItemRecord.getDismissedDate());
            // Store data
            addRecord(businessItemRecord);

        } else {
            Log.d(LOG_TAG, "Item does exist.  Index = "+itemIndex);

            // Update ClickDate
            BusinessItemRecord record = mList.get(itemIndex);
            switch (buttonId){
                case BusinessListAdapter.TOOSOON:
                    record.setTooSoonClickDate(time);
                    break;
                case BusinessListAdapter.DONTLIKE:
                    record.setDontLikeClickDate(time);
                    break;
                case BusinessListAdapter.DISMISS:
                    record.setDismissedDate(time);
                case BusinessListAdapter.LIKE:
                    record.setDontLikeClickDate(time); // Use "-1" for "Like"
            }
            Log.d(LOG_TAG, "Item at index "+itemIndex+" updated");

            // Check
            Log.d(LOG_TAG, record.getId()+" tooSoonClickDate = "+record.getTooSoonClickDate()
                    + "dontlikeClickDate = " + record.getDontLikeClickDate());
        }
    }

    // Returns -1 if item does not exist, otherwise returns index of item
    int getItemIndex(String id){
        int result = -1;
        for (int i=0 ; i<mList.size(); i++){
            if(mList.get(i).getId().equals(id)){
                result = i;
            }
        }
        return result;
    }

    public void commit(){
        // Convert UserRecords to JSON
        Gson gson = new Gson();
        String jsonString = gson.toJson(mList);

        // Check
        Log.d(LOG_TAG, jsonString);

        // Store data
        Log.d(LOG_TAG, "Writing to SharedPreferences...");
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.SharedPrefsFile),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.UserRecordList), jsonString);
        editor.apply();

        // Check
        String check = sharedPreferences.getString(mContext.getString(R.string.UserRecordList), null);
        Log.d(LOG_TAG, "Checking SharedPrefs... "+check);
    }
}
