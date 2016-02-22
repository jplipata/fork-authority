package com.lipata.whatsforlunch.data.user;

import android.util.Log;

import com.lipata.whatsforlunch.data.yelppojo.Business;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlipata on 2/22/16.
 */
public class UserRecordList {
    private static String LOG_TAG = UserRecordList.class.getSimpleName();
    List<BusinessItemRecord> list = new ArrayList<BusinessItemRecord>();

    public void addRecord(BusinessItemRecord businessItemRecord){
        list.add(businessItemRecord);
        Log.d(LOG_TAG, "BusinessItemRecord added");
    }

    public void updateTooSoon(Business business, long time){

        // Let's assume for a sec that the item doesn't exist in user data
        // if the item doesn't exist:
        BusinessItemRecord businessItemRecord = new BusinessItemRecord();
        businessItemRecord.setId(business.getId());
        businessItemRecord.setTooSoonClickDate(time);
        // Check
        Log.d(LOG_TAG,"businessItemRecord.  Id = "+businessItemRecord.getId()+
                " tooSoonClickDate = "+businessItemRecord.getTooSoonClickDate());
        // Store data
        addRecord(businessItemRecord);
    }

}
