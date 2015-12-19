
package com.lipata.testlocationdatafromdevice.data.yelp;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Location {

    private String city;
    private List<String> displayAddress = new ArrayList<String>();
    private double geoAccuracy;
    private List<String> neighborhoods = new ArrayList<String>();
    private String postalCode;
    private String countryCode;
    private List<String> address = new ArrayList<String>();
    private Coordinate coordinate;
    private String stateCode;

    /**
     * 
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The displayAddress
     */
    public List<String> getDisplayAddress() {
        return displayAddress;
    }

    /**
     * 
     * @param displayAddress
     *     The display_address
     */
    public void setDisplayAddress(List<String> displayAddress) {
        this.displayAddress = displayAddress;
    }

    /**
     * 
     * @return
     *     The geoAccuracy
     */
    public double getGeoAccuracy() {
        return geoAccuracy;
    }

    /**
     * 
     * @param geoAccuracy
     *     The geo_accuracy
     */
    public void setGeoAccuracy(double geoAccuracy) {
        this.geoAccuracy = geoAccuracy;
    }

    /**
     * 
     * @return
     *     The neighborhoods
     */
    public List<String> getNeighborhoods() {
        return neighborhoods;
    }

    /**
     * 
     * @param neighborhoods
     *     The neighborhoods
     */
    public void setNeighborhoods(List<String> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

    /**
     * 
     * @return
     *     The postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * 
     * @param postalCode
     *     The postal_code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * 
     * @return
     *     The countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 
     * @param countryCode
     *     The country_code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * 
     * @return
     *     The address
     */
    public List<String> getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(List<String> address) {
        this.address = address;
    }

    /**
     * 
     * @return
     *     The coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * 
     * @param coordinate
     *     The coordinate
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * 
     * @return
     *     The stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * 
     * @param stateCode
     *     The state_code
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

}
