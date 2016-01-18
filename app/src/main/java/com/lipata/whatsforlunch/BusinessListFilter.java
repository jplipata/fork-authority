package com.lipata.whatsforlunch;

import com.lipata.whatsforlunch.data.yelppojo.Business;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlipatap on 1/17/16.
 */
public class BusinessListFilter {
    List<Business> mBusinessList_Source;
    List<Business> mBusinessList_Filtered = new ArrayList<>();

    public BusinessListFilter(List<Business> businessList_Source) {
        this.mBusinessList_Source = businessList_Source;
    }

    List<Business> getFilteredList(){
        // Iterate through list, adjust order according to user preferences
        mBusinessList_Filtered.addAll(mBusinessList_Source);
        for(int i=0; i<mBusinessList_Source.size(); i++){
            Business business = mBusinessList_Source.get(i);
            String businessId = business.getId();

            // Look for this `businessId` in the database

            // Rearrange `mBusinessList_Filtered` as needed

        }
        return mBusinessList_Filtered;
    }


}
