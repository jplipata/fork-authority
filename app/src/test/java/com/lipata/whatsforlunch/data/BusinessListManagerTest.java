package com.lipata.whatsforlunch.data;

import android.content.Context;

import com.lipata.whatsforlunch.api.yelp.model.Business;
import com.lipata.whatsforlunch.data.user.UserRecords;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jlipata on 6/21/16.
 */
public class BusinessListManagerTest {

    @RunWith(MockitoJUnitRunner.class)
    public void testFilter() throws Exception {

        @Mock
        Context mContext;

        mContext = Mockito.mock(Context.class);

        UserRecords userRecords = new UserRecords(mContext); // Needs a Context
        BusinessListManager businessListMgr = new BusinessListManager(mContext , userRecords); // Needs a Context

        // Mock up data - `Business`

        Business businessLiked = new Business();
        businessLiked.setId("Liked");
        businessLiked.setDontLikeClickDate(-1); //-1 means like
        businessLiked.setTooSoonClickDate(0);

        Business businessNeutral = new Business();
        businessNeutral.setId("Neutral");
        // 0 indicates the fields have been unassigned
        businessNeutral.setDontLikeClickDate(0);
        businessNeutral.setTooSoonClickDate(0);

        Business businessDontLiked = new Business();
        businessDontLiked.setId("DontLiked");
        businessDontLiked.setDontLikeClickDate(System.currentTimeMillis()); //Today
        businessDontLiked.setTooSoonClickDate(0);

        Business businessTooSoon = new Business();
        businessTooSoon.setId("TooSoon");
        businessTooSoon.setDontLikeClickDate(0);
        businessTooSoon.setTooSoonClickDate(System.currentTimeMillis());

        // Add the businesses to a list, out of order
        List<Business> listSource = new ArrayList<>();
        listSource.add(businessDontLiked);
        listSource.add(businessNeutral);
        listSource.add(businessTooSoon);
        listSource.add(businessLiked);

        for (int i=0; i<listSource.size(); i++) {
            System.out.println(i + " " + listSource.get(i).getId());
        }

        Assert.assertTrue(Arrays.equals(expectedArray, filter(listSource)));

    }
}