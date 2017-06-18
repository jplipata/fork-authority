package com.lipata.forkauthority.api.yelp3.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jlipata on 6/3/17.
 */

public class TokenResponse {
    @SerializedName("access_token")
    String accessToken;

    @SerializedName("token_type")
    String tokenType;

    @SerializedName("expires_in")
    Integer expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }
}
