package com.lipata.forkauthority.api.yelp3.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("businesses")
    @Expose
    private List<Business> businesses = null;

    public Integer getTotal() {
        return total;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }
}
