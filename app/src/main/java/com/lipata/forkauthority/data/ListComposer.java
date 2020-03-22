package com.lipata.forkauthority.data;

import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.businesslist.BusinessListBaseItem;
import com.lipata.forkauthority.businesslist.BusinessListHeader;
import com.lipata.forkauthority.businesslist.JustAteHereExpiryCalculator;
import com.lipata.forkauthority.data.user.BusinessItemRecord;
import com.lipata.forkauthority.data.user.UserRecords;
import com.lipata.forkauthority.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.lipata.forkauthority.businesslist.BusinessListAdapter.DONTLIKE;

public class ListComposer {

    private UserRecords mUserRecords;
    private JustAteHereExpiryCalculator justAteHereExpiryCalculator;

    @Inject
    public ListComposer(
            final UserRecords userRecords,
            final JustAteHereExpiryCalculator justAteHereExpiryCalculator
    ) {
        this.mUserRecords = userRecords;
        this.justAteHereExpiryCalculator = justAteHereExpiryCalculator;
    }

    /**
     * Takes a list of `Business`s and sorts them according to user preferences stored in `UserRecords`
     *
     * @param businessList_Source List to be sorted.
     * @return Returns separate, sorted lists
     */
    public CombinedList filter(List<Business> businessList_Source) {
        long startTime = System.nanoTime();

        // 3 categories that each business can be filtered to
        List<BusinessListBaseItem> likesList = new ArrayList<>();
        List<BusinessListBaseItem> likedButTooSoonList = new ArrayList<>();
        List<BusinessListBaseItem> unsortedTooSoonList = new ArrayList<>();
        List<BusinessListBaseItem> dontLikeList = new ArrayList<>();

        // Make a copy of the source list
        List<BusinessListBaseItem> businessList_temp = new ArrayList<>(businessList_Source);

        // Get user data
        HashMap<String, BusinessItemRecord> userRecordMap = mUserRecords.getUserRecords();

        // Iterate through API results, adjust order according to user records
        for (int i = 0; i < businessList_Source.size(); i++) {
            Business business = businessList_Source.get(i);
            String businessId = business.getId();

            if (userRecordMap.containsKey(businessId)) {

                BusinessItemRecord businessItemRecord = userRecordMap.get(businessId);

                long tooSoonClickDate = businessItemRecord.getTooSoonClickDate();
                long dontLikeClickDate = businessItemRecord.getDontLikeClickDate();
                long dismissedDate = businessItemRecord.getDismissedDate();
                int dismissedCount = businessItemRecord.getDismissedCount();

                // Calculate difference between current time
                long dontLikeDelta = System.currentTimeMillis() - dontLikeClickDate;
                long dontLikeDelta_days = dontLikeDelta / 1000 / 60 / 60 / 24; // Convert to days

                Timber.d("Match found! Id = " + businessId + " tooSoonClickDate = "
                        + tooSoonClickDate + " dontLikeClickDate = " + dontLikeClickDate +
                        " dismissedDate = " + dismissedDate
                        + " dismissedCount = " + dismissedCount);

                // On match found, do:

                // Update the `business` object in memory
                business.setDontLikeClickDate(dontLikeClickDate);
                business.setTooSoonClickDate(tooSoonClickDate);
                business.setDismissedCount(dismissedCount);

                // Handle Like case

                if (dontLikeClickDate == -1) {

                    // Assign it to the "Liked" list, or the "Liked, but too soon" list

                    if (business.getTooSoonClickDate() == 0 ||
                            justAteHereExpiryCalculator.isExpired(business.getTooSoonClickDate())) {

                        likesList.add(business);
                        businessList_temp.set(i, null); // Remove business from original list
                        Timber.v("filter() deemed LIKED");
                    } else {
                        Timber.v("filter() deemed LIKED BUT TOO SOON");
                        likedButTooSoonList.add(business);
                        businessList_temp.set(i, null); // Remove business from original list
                    }
                }

                // Handle Dont Like case

                if (dontLikeClickDate > 0) {

                    // Add to DontLike list, unless expired
                    if (dontLikeDelta_days < AppSettings.DONTLIKE_THRESHOLD_INDAYS) {
                        // Not expired
                        Timber.v("filter() Deemed DON'T LIKE!");
                        dontLikeList.add(business);
                        businessList_temp.set(i, null); // Remove business from original list
                    } else {
                        // Expired
                        Timber.v("filter() DontLike EXPIRED, not assigned to DONTLIKE list");

                        // Update SharedPrefs
                        Timber.d("filter() DontLike EXPIRED, resetting %s in UserRecords", business.getName());
                        mUserRecords.updateClickDate(business, 0, DONTLIKE);

                        // Update in-memory object
                        business.setDontLikeClickDate(0);
                    }
                }

                // Handle the "Too Soon" case:

                if (tooSoonClickDate != 0 && dontLikeClickDate != -1) {
                    if (!justAteHereExpiryCalculator.isExpired(business.getTooSoonClickDate())) {
                        Timber.v("filter() Deemed too soon, unsorted!");
                        unsortedTooSoonList.add(business);
                        businessList_temp.set(i, null); // Remove business from original list
                    } else Timber.v("filter() TooSoon EXPIRED");
                }
            }
        }

        // Pare down results
        // Let's try only displaying roughly 100 results, I don't think we need more than that
        // Note: This is separate from fetching results from the backend. When fetching results
        // from the backend, you want to fetch a higher number to make sure you don't miss any
        // businesses the user likes
        if (businessList_temp.size() > AppSettings.RESULTS_TO_DISPLAY_MAX) {
            Timber.d("Paring down businessList_temp. Original size " + businessList_temp.size() +
                    ". " + (businessList_temp.size() - AppSettings.RESULTS_TO_DISPLAY_MAX) + " items removed");
            businessList_temp = businessList_temp.subList(0, AppSettings.RESULTS_TO_DISPLAY_MAX);
        }

        // Remove null elements
        businessList_temp.removeAll(Collections.singleton(null));

        // Add headers

        if (likesList.size() > 0) {
            likesList.add(0, new BusinessListHeader(Categories.LIKES));
        }
        if (likedButTooSoonList.size() > 0) {
            likedButTooSoonList.add(0, new BusinessListHeader(Categories.LIKES_TOO_SOON));
        }
        if (businessList_temp.size() > 0) {
            businessList_temp.add(0, new BusinessListHeader(Categories.UNSORTED));
        }
        if (unsortedTooSoonList.size() > 0) {
            unsortedTooSoonList.add(0, new BusinessListHeader(Categories.UNSORTED_TOO_SOON));
        }
        if (dontLikeList.size() > 0) {
            dontLikeList.add(0, new BusinessListHeader(Categories.DONT_LIKE));
        }

        List<List<BusinessListBaseItem>> lists = new ArrayList<>();
        lists.add(likesList);
        lists.add(likedButTooSoonList);
        lists.add(businessList_temp);
        lists.add(unsortedTooSoonList);
        lists.add(dontLikeList);

        CombinedList combinedList = new CombinedList();
        combinedList.setSublists(lists);

        // That's it! Return the filtered lists.
        Utility.reportExecutionTime(this, "BusinessList filter()", startTime);
        return combinedList;
    }
}
