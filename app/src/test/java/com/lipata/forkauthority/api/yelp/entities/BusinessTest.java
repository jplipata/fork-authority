package com.lipata.forkauthority.api.yelp.entities;

import com.lipata.forkauthority.api.yelp3.entities.Business;

import org.junit.Assert;
import org.junit.Test;

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
    public void testGetDescriptiveText_JustAteHere_Solo() throws Exception {
        final long DAY = 86400000L; // 1 day in ms
        business = new Business();
        business.setDontLikeClickDate(0); // Sets to unassigned

        // Set tooSoonClickDate to 1 day ago
        business.setTooSoonClickDate(System.currentTimeMillis() - (DAY*1)); //
        Assert.assertEquals("You ate here very recently", business.getDescriptiveText());

        // Set tooSoonClickDate to 3 days ago
        business.setTooSoonClickDate(System.currentTimeMillis() - (DAY*3)); //
        Assert.assertEquals("You ate here roughly 3 days ago", business.getDescriptiveText());

        // Set tooSoonClickDate to 30 days ago
        business.setTooSoonClickDate(System.currentTimeMillis() - (DAY*30)); //
        Assert.assertEquals("You ate here roughly 30 days ago", business.getDescriptiveText());

    }

}