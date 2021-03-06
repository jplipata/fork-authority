package com.lipata.forkauthority.data.user;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lipata.forkauthority.R;
import com.lipata.forkauthority.api.yelp3.entities.Business;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by jlipata on 11/6/17.
 */

@RunWith(AndroidJUnit4.class)
public class UserRecordsTest {

    private UserRecords userRecords;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();

        userRecords = new UserRecords(
                context,
                context.getSharedPreferences(
                        context.getString(R.string.test_shared_prefs_file), // Using a test sharedprefs file
                        Context.MODE_PRIVATE)
        );
    }


    @Test
    public void incrementDismissedCount() throws Exception {
        final String BUSINESS_ID = "test-business";
        int initialDismissedCount;

        if (userRecords.getUserRecords().containsKey(BUSINESS_ID)) {
            initialDismissedCount = userRecords.getUserRecords().get(BUSINESS_ID).getDismissedCount();
        } else {
            Business business = new Business();
            business.setId(BUSINESS_ID);

            // At this time, we don't have a method to simply add a record,
            // this will add the record and set dismissedCount to 1 if it doesn't already exist
            userRecords.incrementDismissedCount(BUSINESS_ID);
            initialDismissedCount = userRecords
                    .getUserRecords()
                    .get(BUSINESS_ID)
                    .getDismissedCount();
        }

        userRecords.incrementDismissedCount(BUSINESS_ID);

        int newDismissedCount = userRecords.getUserRecords().get(BUSINESS_ID).getDismissedCount();

        assertThat(newDismissedCount, is(initialDismissedCount + 1));
    }

}