package com.lipata.forkauthority.data.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lipata.forkauthority.R;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.di.PerApp;
import com.lipata.forkauthority.ui.BusinessListAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;

import javax.inject.Inject;

import timber.log.Timber;

@PerApp
public class UserRecords {

    private Context mContext;
    private SharedPreferences sharedPrefs;
    private Type collectionType;
    private Gson gson;

    private HashMap<String, BusinessItemRecord> map;

    @Inject
    UserRecords(final Context context, final SharedPreferences sharedPrefs) {
        this.mContext = context;
        this.sharedPrefs = sharedPrefs;

        gson = new Gson();
        collectionType = new TypeToken<HashMap<String, BusinessItemRecord>>(){}.getType();

        // If there's an existing list of records, load it
        if (sharedPrefs.contains(mContext.getString(R.string.key_user_records_v2))) {
            try {
                HashMap<String, BusinessItemRecord> userRecordMap = gson.fromJson(sharedPrefs
                        .getString(mContext.getString(R.string.key_user_records_v2), null), collectionType);
                map = userRecordMap;
            } catch (JsonSyntaxException e) {
                map = new HashMap<>();
            }
        }
        // If there's no existing UserRecords
        else {
            map = new HashMap<>();
        }
    }

    public HashMap<String, BusinessItemRecord> getUserRecords() {
        return map;
    }

    /**
     *
     * @param businessId Business to update
     */
    public void incrementDismissedCount(String businessId) {
        Timber.d("incrementDismissedCount()");

        if (!map.containsKey(businessId)) {
            Timber.d("Item does not exist");

            BusinessItemRecord businessItemRecord = new BusinessItemRecord();
            businessItemRecord.setId(businessId);

            // Increment
            businessItemRecord.incrementDismissedCount();

            // Store data
            updateStores(businessItemRecord);

        } else {
            Timber.d("Item does exist.");

            // Update dismissedCount
            BusinessItemRecord record = map.get(businessId);
            record.incrementDismissedCount();

            // Store data
            updateStores(record);

            Timber.d("Item " + businessId + " updated");
        }

    }

    /**
     * To un-"Don't Like", set `time` to 0
     *
     * @param business
     * @param time
     * @param buttonId
     */
    public void updateClickDate(Business business, long time, int buttonId) {
        Timber.d("updateClickDate()");

        if (!map.containsKey(business.getId())) {
            Timber.d(business.getId() + " - Item does not exist");

            BusinessItemRecord businessItemRecord = new BusinessItemRecord();
            businessItemRecord.setId(business.getId());

            // Assign based on button type
            switch (buttonId) {
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

            updateStores(businessItemRecord);

        } else {
            Timber.d("Item does exist.");

            // Update record ClickDate
            BusinessItemRecord record = map.get(business.getId());
            switch (buttonId) {
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

            updateStores(record);
        }
    }

    private void insertUserRecord(BusinessItemRecord businessItemRecord) {
        map.put(businessItemRecord.getId(), businessItemRecord);

        Timber.d("BusinessItemRecord " + businessItemRecord.getId() + " updated " + map.get(businessItemRecord));
    }

    private void updateStores(BusinessItemRecord businessItemRecord) {
        insertUserRecord(businessItemRecord);
        updateSharedPrefs();
    }

    private void updateSharedPrefs() {
        // Convert UserRecords to JSON
        String jsonString = gson.toJson(map, collectionType);

        // Check
        Timber.d(jsonString);

        // Store data
        Timber.d("Writing to SharedPreferences...");
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(mContext.getString(R.string.key_user_records_v2), jsonString);
        editor.apply();
    }
}
