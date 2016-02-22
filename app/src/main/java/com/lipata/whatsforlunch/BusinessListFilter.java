package com.lipata.whatsforlunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lipata.whatsforlunch.data.user.BusinessItemRecord;
import com.lipata.whatsforlunch.data.yelppojo.Business;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlipatap on 1/17/16.
 */
public class BusinessListFilter {
    private static String LOG_TAG = BusinessListFilter.class.getSimpleName();
    List<Business> mBusinessList_Source;
    List<Business> mBusinessList_Filtered = new ArrayList<>();
    Context mContext;

    public BusinessListFilter(List<Business> businessList_Source, Context context) {
        this.mBusinessList_Source = businessList_Source;
        this.mContext = context;
    }

    List<Business> filter(){
        // Get user data
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.SharedPrefsFile)
                , Context.MODE_PRIVATE);
        String userRecordListJson = sharedPreferences.getString(mContext.getString(R.string.UserRecordList), null);
        Log.d(LOG_TAG, "SharedPrefs get UserRecordList string " + userRecordListJson);

        // Since it comes back in JSON, we need to convert it to Java Objects
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<BusinessItemRecord>>(){}.getType();
        ArrayList<BusinessItemRecord> userRecordList = gson.fromJson(userRecordListJson, collectionType );

        // Iterate through list from API, adjust order according to user preferences
        mBusinessList_Filtered.addAll(mBusinessList_Source);
        for(int i=0; i<mBusinessList_Source.size(); i++){
            Business business = mBusinessList_Source.get(i);
            String businessId = business.getId();

            // Look for this `businessId` in the database
            for (int j=0; j<userRecordList.size(); j++){
                BusinessItemRecord businessItemRecord = userRecordList.get(j);
                if(businessItemRecord.getId().equals(businessId)){
                    long tooSoonClickDate = businessItemRecord.getTooSoonClickDate();
                    int rating = businessItemRecord.getRating();
                    Log.d(LOG_TAG, "Match found. Id = "+businessId+" tooSoonClickDate = "+tooSoonClickDate+" rating = "+rating);
                }
            }



            // Rearrange `mBusinessList_Filtered` as needed

        }
        return mBusinessList_Filtered;
    }


}
