package com.lipata.forkauthority.util

import android.location.Address
import com.google.gson.Gson
import org.junit.Test

class AddressParserTest {
    // TODO Create one more test for Address with sublocality

    /**
     * Test case for Address with locality instead of sublocality
     */
    @Test
    fun getFormattedAddress() {
        val testObject = "{\"mAddressLines\":{\"0\":\"11000 Terminal Access Rd, Fort Myers, FL 33913, USA\"},\"mAdminArea\":\"Florida\",\"mCountryCode\":\"US\",\"mCountryName\":\"United States\",\"mFeatureName\":\"11000\",\"mHasLatitude\":true,\"mHasLongitude\":true,\"mLatitude\":26.5285284,\"mLocale\":\"en_US\",\"mLocality\":\"Fort Myers\",\"mLongitude\":-81.755196,\"mMaxAddressLineIndex\":0,\"mPostalCode\":\"33913\",\"mSubAdminArea\":\"Lee County\",\"mSubThoroughfare\":\"11000\",\"mThoroughfare\":\"Terminal Access Road\"}"
        val expectedOutput = "Fort Myers, Florida 33913"
        val address = Gson().fromJson(testObject, Address::class.java)

        // Method under test
        val formattedAddress = AddressParser().getFormattedAddress(address)

        assert(formattedAddress == expectedOutput)
    }
}