package com.lipata.forkauthority.util

import android.location.Address

class AddressParser {

    /**
     * @param address Takes {@link android.location.Address}
     * @return Returns a string formatted with US `<city>, <state> <zip>`
     */
    fun getFormattedAddress(address: Address): String {
        var formattedString = ""

        val city = getCity(address)
        val state = getState(address)
        val zip = getZip(address)

        if (zip != null) {
            formattedString = zip
        }

        if (state != null) {
            formattedString = state + " " + formattedString
        }

        if (city != null) {
            formattedString = city + ", " + formattedString
        }

        return formattedString
    }

    private fun getCity(address: Address): String? {
        if (address.subLocality != null && address.subLocality.isNotEmpty()) return address.subLocality
        else if (address.locality != null && address.locality.isNotEmpty()) return address.locality
        else return null
    }

    private fun getState(address: Address): String? {
        if (address.adminArea != null && address.adminArea.isNotEmpty()) return address.adminArea
        else return null
    }

    private fun getZip(address: Address): String? {
        if (address.postalCode != null && address.postalCode.isNotEmpty()) return address.postalCode
        else return null
    }
}