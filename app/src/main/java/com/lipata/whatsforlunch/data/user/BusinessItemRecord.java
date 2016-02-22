package com.lipata.whatsforlunch.data.user;

/**
 * Created by jlipata on 2/22/16.
 */
public class BusinessItemRecord {

    String Id;
    long tooSoonClickDate;
    int rating;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public long getTooSoonClickDate() {
        return tooSoonClickDate;
    }

    public void setTooSoonClickDate(long tooSoonClickDate) {
        this.tooSoonClickDate = tooSoonClickDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


}
