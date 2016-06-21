package com.lipata.whatsforlunch.api.yelp.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jlipata on 6/21/16.
 */
public class BusinessTest {

    final long ONE_DAY=86400000; // One day in milliseconds

    Business business;

    @Before
    public void setUp() throws Exception {


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetDescriptiveText_LikeCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(-1); // Sets to "Like"
        business.setTooSoonClickDate(0); // Sets Just Ate Here

        Assert.assertEquals(Business.YOU_LIKE_THIS, business.getDescriptiveText());
    }

    @Test
    public void testGetDescriptiveText_DontLikeCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(System.currentTimeMillis()-ONE_DAY); // Sets to "Don't Like" as of yesterday
        business.setTooSoonClickDate(0); // Sets Just Ate Here

        Assert.assertEquals(Business.DONT_LIKE_THIS, business.getDescriptiveText());
    }

    @Test
    public void testGetDescriptiveText_NullCase() throws Exception {
        business = new Business();
        business.setDontLikeClickDate(0); // Sets to "Don't Like" as of yesterday
        business.setTooSoonClickDate(0); // Sets Just Ate Here

        Assert.assertNull(business.getDescriptiveText());
    }

}