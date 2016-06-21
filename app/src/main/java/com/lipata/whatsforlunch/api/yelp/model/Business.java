
package com.lipata.whatsforlunch.api.yelp.model;

import com.google.gson.annotations.SerializedName;
import com.lipata.whatsforlunch.Utility;
import com.lipata.whatsforlunch.data.AppSettings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Business {

    // Defining these display strings here because using res/values/strings.xml requires a `Context`
    public static final String YOU_LIKE_THIS = "You like this";
    public static final String DONT_LIKE_THIS = "Don't like";
    public static final String JUST_ATE_HERE_SOLO = "Just ate here on ";
    public static final String JUST_ATE_HERE_APPENDED = ".  Just ate here on ";
    public static final String ATE_HERE_SOLO = "You ate here on ";
    public static final String ATE_HERE_APPENDED = ".  You ate here on ";

    private boolean isClaimed;
    private double rating;
    private String mobileUrl;
    private String ratingImgUrl;

    @SerializedName("review_count")
    private int reviewCount;

    private String name;
    private String snippetImageUrl;
    private String ratingImgUrlSmall;
    private String url;
    private List<List<String>> categories = new ArrayList<List<String>>();
    private int menuDateUpdated;
    private String phone;
    private String snippetText;

    @SerializedName("image_url")
    private String imageUrl;

    private Location location;

    @SerializedName("display_phone")
    private String displayPhone;

    @SerializedName("rating_img_url_large")
    private String ratingImgUrlLarge;

    private String menuProvider;
    private String id;
    private boolean isClosed;
    private double distance;

    // My fields

    /**
     * Field used to track "just ate here" as a date.
     * Initializes as 0 when converted by GSON
     */
    long tooSoonClickDate;

    /**
     * Field used to track Like or Don't Like state.
     * Initializes as 0 when converted by GSON
     * -1 means "Like"
     * A positive number means "Don't Like", expressed as a date when it was clicked (so that it can expire after a certain amount of time)
     */
    long dontLikeClickDate;

    /**
     * Initializes as 0 when converted by GSON
     * Not in use since dismiss functionality was changed to not expire. Under consideration for removal.
     */
    long dismissedDate;


    // Public methods

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

        // Case for Just Ate Here-Not Expired
        String monthDayYear = Utility.formatDate(tooSoonClickDate);
        if(tooSoonClickDate!=0 && !isTooSoonClickDateExpired()) {
            if(dontLikeClickDate==0){
                stringBuilder.append(JUST_ATE_HERE_SOLO+monthDayYear);
            } else {
                stringBuilder.append(JUST_ATE_HERE_APPENDED+monthDayYear);
            }
        }

        // Case for Just Ate Here-Expired
        else if(tooSoonClickDate!=0 && isTooSoonClickDateExpired()){
            if(dontLikeClickDate==0){
                stringBuilder.append(ATE_HERE_SOLO+monthDayYear);
            } else {
                stringBuilder.append(ATE_HERE_APPENDED+monthDayYear);
            }
        }

        return stringBuilder.toString();
    }

    public boolean isTooSoonClickDateExpired(){
        long now = System.currentTimeMillis();
        if(now-AppSettings.TOOSOON_THRESHOLD>tooSoonClickDate) return true;
        else return false;
    }

    /**
     * `categories` field from JSON is an ArrayList.  This method formats it into a String for display to UI
     */
    public String getFormattedCategories() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<categories.size(); i++){
            String category = categories.get(i).get(0);
            stringBuilder.append(category);
            if(i<(categories.size()-1)){
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public long getTooSoonClickDate() {
        return tooSoonClickDate;
    }
    
    public long getDontLikeClickDate() {
        return dontLikeClickDate;
    }

    public long getDismissedDate() {
        return dismissedDate;
    }

    public void setTooSoonClickDate(long tooSoonClickDate) {
        this.tooSoonClickDate = tooSoonClickDate;
    }

    public void setDontLikeClickDate(long dontLikeClickDate) {
        this.dontLikeClickDate = dontLikeClickDate;
    }

    public void setDismissedDate(long dismissedDate) {
        this.dismissedDate = dismissedDate;
    }


    // Autogenerated getters and setters

    /**
     * 
     * @return
     *     The isClaimed
     */
    public boolean isIsClaimed() {
        return isClaimed;
    }

    /**
     * 
     * @param isClaimed
     *     The is_claimed
     */
    public void setIsClaimed(boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    /**
     * 
     * @return
     *     The rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * 
     * @param rating
     *     The rating
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * 
     * @return
     *     The mobileUrl
     */
    public String getMobileUrl() {
        return mobileUrl;
    }

    /**
     * 
     * @param mobileUrl
     *     The mobile_url
     */
    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    /**
     * 
     * @return
     *     The ratingImgUrl
     */
    public String getRatingImgUrl() {
        return ratingImgUrl;
    }

    /**
     * 
     * @param ratingImgUrl
     *     The rating_img_url
     */
    public void setRatingImgUrl(String ratingImgUrl) {
        this.ratingImgUrl = ratingImgUrl;
    }

    /**
     * 
     * @return
     *     The reviewCount
     */
    public int getReviewCount() {
        return reviewCount;
    }

    /**
     * 
     * @param reviewCount
     *     The review_count
     */
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The snippetImageUrl
     */
    public String getSnippetImageUrl() {
        return snippetImageUrl;
    }

    /**
     * 
     * @param snippetImageUrl
     *     The snippet_image_url
     */
    public void setSnippetImageUrl(String snippetImageUrl) {
        this.snippetImageUrl = snippetImageUrl;
    }

    /**
     * 
     * @return
     *     The ratingImgUrlSmall
     */
    public String getRatingImgUrlSmall() {
        return ratingImgUrlSmall;
    }

    /**
     * 
     * @param ratingImgUrlSmall
     *     The rating_img_url_small
     */
    public void setRatingImgUrlSmall(String ratingImgUrlSmall) {
        this.ratingImgUrlSmall = ratingImgUrlSmall;
    }

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The categories
     */
    public List<List<String>> getCategories() {
        return categories;
    }

    /**
     * 
     * @param categories
     *     The categories
     */
    public void setCategories(List<List<String>> categories) {
        this.categories = categories;
    }

    /**
     * 
     * @return
     *     The menuDateUpdated
     */
    public int getMenuDateUpdated() {
        return menuDateUpdated;
    }

    /**
     * 
     * @param menuDateUpdated
     *     The menu_date_updated
     */
    public void setMenuDateUpdated(int menuDateUpdated) {
        this.menuDateUpdated = menuDateUpdated;
    }

    /**
     * 
     * @return
     *     The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 
     * @param phone
     *     The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 
     * @return
     *     The snippetText
     */
    public String getSnippetText() {
        return snippetText;
    }

    /**
     * 
     * @param snippetText
     *     The snippet_text
     */
    public void setSnippetText(String snippetText) {
        this.snippetText = snippetText;
    }

    /**
     * 
     * @return
     *     The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 
     * @param imageUrl
     *     The image_url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 
     * @return
     *     The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * 
     * @return
     *     The displayPhone
     */
    public String getDisplayPhone() {
        return displayPhone;
    }

    /**
     * 
     * @param displayPhone
     *     The display_phone
     */
    public void setDisplayPhone(String displayPhone) {
        this.displayPhone = displayPhone;
    }

    /**
     * 
     * @return
     *     The ratingImgUrlLarge
     */
    public String getRatingImgUrlLarge() {
        return ratingImgUrlLarge;
    }

    /**
     * 
     * @param ratingImgUrlLarge
     *     The rating_img_url_large
     */
    public void setRatingImgUrlLarge(String ratingImgUrlLarge) {
        this.ratingImgUrlLarge = ratingImgUrlLarge;
    }

    /**
     * 
     * @return
     *     The menuProvider
     */
    public String getMenuProvider() {
        return menuProvider;
    }

    /**
     * 
     * @param menuProvider
     *     The menu_provider
     */
    public void setMenuProvider(String menuProvider) {
        this.menuProvider = menuProvider;
    }

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The isClosed
     */
    public boolean isIsClosed() {
        return isClosed;
    }

    /**
     * 
     * @param isClosed
     *     The is_closed
     */
    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    /**
     * 
     * @return
     *     The distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * 
     * @param distance
     *     The distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

}
