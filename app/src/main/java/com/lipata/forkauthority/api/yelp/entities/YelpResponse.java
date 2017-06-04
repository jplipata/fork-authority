
package com.lipata.forkauthority.api.yelp.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class YelpResponse {

    private Region region;
    private int total;
    private List<Business> businesses = new ArrayList<Business>();

    @SerializedName("error")
    private Error error;

    /**
     *
     * @return
     * The error
     */
    public Error getError() {
        return error;
    }

    /**
     *
     * @param error
     * The error
     */
    public void setError(Error error) {
        this.error = error;
    }


    /**
     * 
     * @return
     *     The region
     */
    public Region getRegion() {
        return region;
    }

    /**
     * 
     * @param region
     *     The region
     */
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * 
     * @return
     *     The total
     */
    public int getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 
     * @return
     *     The businesses
     */
    public List<Business> getBusinesses() {
        return businesses;
    }

    /**
     * 
     * @param businesses
     *     The businesses
     */
    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

}
