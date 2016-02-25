package com.lipata.whatsforlunch.data.user;

/**
 * Created by jlipata on 2/22/16.
 */
public class BusinessItemRecord {

    String Id;
    long tooSoonClickDate;
    long dontLikeClickDate;
    long dismissedClickDate;

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

    public long getDontLikeClickDate() {
        return dontLikeClickDate;
    }

    public void setDontLikeClickDate(long dontLikeClickDate) {
        this.dontLikeClickDate = dontLikeClickDate;
    }

    public long getDismissedDate() {
        return dismissedClickDate;
    }

    public void setDismissedDate(long dismissedClickDate) {
        this.dismissedClickDate = dismissedClickDate;
    }

}
