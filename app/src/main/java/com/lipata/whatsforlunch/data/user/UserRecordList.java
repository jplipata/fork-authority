package com.lipata.whatsforlunch.data.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.lipata.whatsforlunch.R;
import com.lipata.whatsforlunch.data.yelppojo.Business;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlipata on 2/22/16.
 */
public class UserRecordList {
    private static String LOG_TAG = UserRecordList.class.getSimpleName();
    List<BusinessItemRecord> mList;
    Context mContext;

    public UserRecordList(Context context) {
        mList = new ArrayList<BusinessItemRecord>();
        this.mContext=context;
    }

    public void addRecord(BusinessItemRecord businessItemRecord){
        mList.add(businessItemRecord);
        Log.d(LOG_TAG, "BusinessItemRecord added");
    }

    public void updateTooSoon(Business business, long time){
        Log.d(LOG_TAG, "updateTooSoon()");

        // Check for item
        int itemIndex = getItemIndex(business.getId());
        Log.d(LOG_TAG, "getItemIndex() "+itemIndex);

        // -1 means item does not exist
        if(itemIndex==-1){
            Log.d(LOG_TAG, "Item does not exist");
            // if the item doesn't exist:
            BusinessItemRecord businessItemRecord = new BusinessItemRecord();
            businessItemRecord.setId(business.getId());
            businessItemRecord.setTooSoonClickDate(time);
            // Check
            Log.d(LOG_TAG, "businessItemRecord.  Id = " + businessItemRecord.getId() +
                    " tooSoonClickDate = " + businessItemRecord.getTooSoonClickDate());
            // Store data
            addRecord(businessItemRecord);

        } else {
            Log.d(LOG_TAG, "Item does exist.  Index = "+itemIndex);

            // Update TooSoonClickDate
            BusinessItemRecord record = mList.get(itemIndex);
            record.setTooSoonClickDate(time);
            Log.d(LOG_TAG, "Item at index "+itemIndex+" updated");

            // Check
            Log.d(LOG_TAG, record.getId()+" tooSoonClickDate = "+record.getTooSoonClickDate());
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
        // Convert UserRecordList to JSON
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
        editor.commit();

        // Check
        String check = sharedPreferences.getString(mContext.getString(R.string.UserRecordList), null);
        Log.d(LOG_TAG, "Checking SharedPrefs... "+check);
    }
}
