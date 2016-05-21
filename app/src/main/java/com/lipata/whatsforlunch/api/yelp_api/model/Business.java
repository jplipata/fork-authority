
package com.lipata.whatsforlunch.api.yelp_api.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Business {

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
    long tooSoonClickDate;
    long dontLikeClickDate;
    long dismissedDate;


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
