package com.lipata.forkauthority.data.user;

public class BusinessItemRecord {

    // Status flags
    public static final int LIKED = -1; // I'm using the dontLikeClickDate field for "Like" status.  "-1" means "Like"
    public static final int UNSORTED = 0;

    private String Id;

    /**
     * tooSoonClickDate
     * Unit: milliseconds
     */
    private long tooSoonClickDate;

    private long dontLikeClickDate; // A value of "-1" means "Like"
    private long dismissedClickDate;

    private int dismissedCount;

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
