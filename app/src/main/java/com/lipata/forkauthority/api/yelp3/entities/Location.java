package com.lipata.forkauthority.api.yelp3.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("address1")
    @Expose
    private String address1;

    @SerializedName("address2")
    @Expose
    private String address2;

    @SerializedName("address3")
    @Expose
    private String address3;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("zip_code")
    @Expose
    private String zipCode;

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }
}
