package com.lipata.forkauthority.api.yelp3.entities;

/**
 * Created by jlipata on 6/4/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("alias")
    @Expose
    public String alias;
    @SerializedName("title")
    @Expose
    public String title;

    public String getTitle() {
        return title;
    }
}
