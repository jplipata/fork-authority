
package com.lipata.whatsforlunch.yelp_apiresponsepojo;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Span {

    private double latitudeDelta;
    private double longitudeDelta;

    /**
     * 
     * @return
     *     The latitudeDelta
     */
    public double getLatitudeDelta() {
        return latitudeDelta;
    }

    /**
     * 
     * @param latitudeDelta
     *     The latitude_delta
     */
    public void setLatitudeDelta(double latitudeDelta) {
        this.latitudeDelta = latitudeDelta;
    }

    /**
     * 
     * @return
     *     The longitudeDelta
     */
    public double getLongitudeDelta() {
        return longitudeDelta;
    }

    /**
     * 
     * @param longitudeDelta
     *     The longitude_delta
     */
    public void setLongitudeDelta(double longitudeDelta) {
        this.longitudeDelta = longitudeDelta;
    }

}
