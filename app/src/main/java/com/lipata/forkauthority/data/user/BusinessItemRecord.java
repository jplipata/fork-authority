package com.lipata.forkauthority.data.user;

public class BusinessItemRecord {

    public static final int LIKE_FLAG = -1; // I'm using the dontLikeClickDate field for "Like" status.  "-1" means "Like"

    String Id;

    /**
     * tooSoonClickDate
     * Unit: milliseconds
     */
    long tooSoonClickDate;

    long dontLikeClickDate; // A value of "-1" means "Like"
    long dismissedClickDate;

    int dismissedCount;

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

    public int getDismissedCount() {
        return dismissedCount;
    }

    public void incrementDismissedCount() {
        dismissedCount++;
    }

}
