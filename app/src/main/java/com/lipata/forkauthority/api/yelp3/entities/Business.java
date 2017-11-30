package com.lipata.forkauthority.api.yelp3.entities;

/**
 * Created by jlipata on 6/4/17.
 */

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lipata.forkauthority.util.Utility;
import com.lipata.forkauthority.data.AppSettings;

import java.util.List;

public class Business {

    public static final String YOU_LIKE_THIS = "You like this";
    public static final String DONT_LIKE_THIS = "Don't like";
    public static final String ATE_HERE_PLURAL_SOLO = "You ate here roughly %d days ago";
    public static final String ATE_HERE_PLURAL_APPENDED_ = ".  You ate here roughly %d days ago";
    public static final String ATE_HERE_SINGULAR_SOLO_ = "You ate here very recently";
    public static final String ATE_HERE_SINGULAR_APPENDED = ".  You ate here very recently";


    @SerializedName("rating")
    @Expose
    private String rating;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("is_closed")
    @Expose
    public Boolean isClosed;

    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;

    @SerializedName("review_count")
    @Expose
    private Integer reviewCount;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("coordinates")
    @Expose
    public Coordinates coordinates;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("location")
    @Expose
    public Location location;

    @SerializedName("distance")
    @Expose
    public Float distance;

    @SerializedName("transactions")
    @Expose
    public List<String> transactions = null;

    /**
     * Field used to track "just ate here" as a date.
     * Initializes as 0 when converted by GSON
     */
    private long tooSoonClickDate;

    /**
     * Field used to track Like or Don't Like state.
     * Initializes as 0 when converted by GSON
     * -1 means "Like"
     * A positive number means "Don't Like", expressed as a date when it was clicked (so that it can expire after a certain amount of time)
     */
    private long dontLikeClickDate;

    /**
     * Initializes as 0 when converted by GSON
     * Not in use since dismiss functionality was changed to not expire. Under consideration for removal.
     */
    private long dismissedDate;

    private int dismissedCount;

    public boolean isTooSoonClickDateExpired() {
        long now = System.currentTimeMillis();
        if (now - AppSettings.TOOSOON_THRESHOLD > tooSoonClickDate) return true;
        else return false;
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
        return dismissedDate;
    }

    public void setDismissedDate(long dismissedDate) {
        this.dismissedDate = dismissedDate;
    }

    public int getDismissedCount() {
        return dismissedCount;
    }

    public void setDismissedCount(int dismissedCount) {
        this.dismissedCount = dismissedCount;
    }

    /**
     * @return Returns supplementary text that explains the user's preferences for this business.  Returns `null` if the preferences haven't been set.
     */
    public String getDescriptiveText(){
        StringBuilder stringBuilder = new StringBuilder();

        // Case where neither field has been set
        if(dontLikeClickDate==0 && tooSoonClickDate==0){
            return null;
        }

        // Case where Like/Don't Like has been set.
        if(dontLikeClickDate==-1){
            stringBuilder.append(YOU_LIKE_THIS);
        } else if(dontLikeClickDate!=0){
            stringBuilder.append(DONT_LIKE_THIS);
        }


        // Case for Just Ate Here (Expired and Not Expired)
        String monthDayYear = Utility.formatDate(tooSoonClickDate);
        int days = (int) ((System.currentTimeMillis()-tooSoonClickDate) / 86400000); // Number of milliseconds in a day
        if(tooSoonClickDate!=0) {
            if(days>1) {
                if (dontLikeClickDate == 0) {
                    String string = String.format(ATE_HERE_PLURAL_SOLO, days);
                    stringBuilder.append(string);
                } else {
                    String string = String.format(ATE_HERE_PLURAL_APPENDED_, days);
                    stringBuilder.append(string);
                }
            } else {
                if (dontLikeClickDate == 0) {
                    stringBuilder.append(ATE_HERE_SINGULAR_SOLO_);
                } else {
                    stringBuilder.append(ATE_HERE_SINGULAR_APPENDED);
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     * `categories` field from JSON is an ArrayList.  This method formats it into a String for display to UI
     */
    public String getFormattedCategories() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<categories.size(); i++){
            String category = categories.get(i).getTitle();
            stringBuilder.append(category);
            if(i<(categories.size()-1)){
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public Location getLocation() {
        return location;
    }

    public String getRating() {
        Log.v("Business", "getRating() " + rating);
        return rating;
    }
}