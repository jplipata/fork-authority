package com.lipata.whatsforlunch.api.yelp_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by jlipata on 3/27/16.
 */
@Generated("org.jsonschema2pojo")
public class Error {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     *
     * @return
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

}
