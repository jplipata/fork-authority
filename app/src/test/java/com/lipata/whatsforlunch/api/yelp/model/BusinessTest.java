package com.lipata.whatsforlunch.api.yelp.model;

import com.lipata.whatsforlunch.Utility;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jlipata on 6/21/16.
 */
public class BusinessTest {

    final long ONE_DAY=86400000; // One day in milliseconds

    Business business;

    @Test
    public void testGetDescriptiveText_LikeCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(-1); // -1 means "Like"
        business.setTooSoonClickDate(0); // 0 is unassigned

        Assert.assertEquals(Business.YOU_LIKE_THIS, business.getDescriptiveText());
    }

    @Test
    public void testGetDescriptiveText_DontLikeCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(System.currentTimeMillis()-ONE_DAY); // Sets to "Don't Like" as of yesterday
        business.setTooSoonClickDate(0); // Unassigned

        Assert.assertEquals(Business.DONT_LIKE_THIS, business.getDescriptiveText());
    }

    @Test
    public void testGetDescriptiveText_NullCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(0); // Sets to unassigned
        business.setTooSoonClickDate(0); // Sets to unassigned

        Assert.assertNull(business.getDescriptiveText());
    }

    @Test
    public void testGetDescriptiveText_JustAteHereSolo_ExpiredCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(0); // Sets to unassigned
        business.setTooSoonClickDate(1465446266588L); // Sets Just Ate Here to 6/9/2016 *I THINK*, i.e. this is from my device's system time, not sure if it will be universal for other devices

        Assert.assertEquals(Business.ATE_HERE_SOLO+"6/9/2016" ,business.getDescriptiveText());
    }

    @Test
    public void testGetDescriptiveText_JustAteHereSolo_NotExpiredCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(0); // Sets to unassigned

        long now = System.currentTimeMillis();
        business.setTooSoonClickDate(now); // Sets Just Ate Here to today

        Assert.assertEquals(Business.JUST_ATE_HERE_SOLO+ Utility.formatDate(now),business.getDescriptiveText());
    }

}