package com.lipata.forkauthority.ui;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lipata.forkauthority.R;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.data.Categories;
import com.lipata.forkauthority.data.CombinedList;
import com.lipata.forkauthority.data.user.UserRecords;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

import static com.lipata.forkauthority.data.user.BusinessItemRecord.LIKED;

/**
 * Created by jlipata on 1/1/16.
 * TODO There is a better way to handle multiple view types with a "Delegate" pattern.
 * TODO This is a mess. Clean it up!
 */
public class BusinessListAdapter extends RecyclerView.Adapter<BusinessListAdapter.ViewHolder> {

    // Button IDs
    public static final int LIKE = 0;
    public static final int TOOSOON = 1;
    public static final int DONTLIKE = 2;
    public static final int DISMISS = 3;

    // For brevity, due to ugliness of Kotlin interop
    private static final String LIKES_KEY = Categories.Companion.getLIKES();
    private static final String LIKED_TOO_SOON_KEY = Categories.Companion.getLIKES_TOO_SOON();
    private static final String UNSORTED_KEY = Categories.Companion.getUNSORTED();
    private static final String UNSORTED_TOO_SOON_KEY = Categories.Companion.getUNSORTED_TOO_SOON();
    private static final String DONT_LIKE_KEY = Categories.Companion.getDONT_LIKE();

    private CombinedList mBusinessList;
    private BusinessListParentView parentView;
    private RecyclerView.LayoutManager mLayoutManager;
    private UserRecords mUserRecords; // TODO Get this out of the adapter

    BusinessListAdapter(final BusinessListParentView parentView,
                        final UserRecords userRecords) {
        this.parentView = parentView;
        this.mUserRecords = userRecords;
        this.mLayoutManager = parentView.getRecyclerViewLayoutManager();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class BusinessViewHolder extends ViewHolder {
        LinearLayout mLayout_BusinessHeader;
        CardView mCardView_CardView;
        ImageView mImageView_BusinessImage;
        TextView mTextView_BusinessName;
        TextView mTextView_BusinessCategories;
        TextView mTextView_BusinessAddress;
        ImageView mImageView_BusinessRatingUrl;
        TextView mTextView_BusinessReviewCount;

        Button mButton_TooSoon;
        Button mButton_Like;
        Button mButton_DontLike;

        TextView mTextView_DescriptiveText;

        BusinessViewHolder(View v) {
            super(v);
            mLayout_BusinessHeader = v.findViewById(R.id.business_header_layout);
            mCardView_CardView = v.findViewById(R.id.card_view);
            mImageView_BusinessImage = v.findViewById(R.id.business_image);
            mImageView_BusinessImage.setClipToOutline(true); // for rounded corners.  See https://stackoverflow.com/questions/31675420/set-round-corner-image-in-imageview
            mTextView_BusinessName = v.findViewById(R.id.business_name);
            mTextView_BusinessCategories = v.findViewById(R.id.business_categories);
            mTextView_BusinessAddress = v.findViewById(R.id.business_address);
            mImageView_BusinessRatingUrl = v.findViewById(R.id.business_rating);
            mTextView_BusinessReviewCount = v.findViewById(R.id.business_review_count);

            mButton_TooSoon = v.findViewById(R.id.button_toosoon);
            mButton_Like = v.findViewById(R.id.button_like);
            mButton_DontLike = v.findViewById(R.id.button_dontlike);

            mTextView_DescriptiveText = v.findViewById(R.id.business_descriptive_text);
        }
    }

    static class HeaderViewHolder extends ViewHolder {
        TextView mTextView_Header;

        HeaderViewHolder(View v) {
            super(v);
            mTextView_Header = v.findViewById(R.id.header);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return mBusinessList.get(position).getViewType();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case ListItemTypes.HEADER: {
                View itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.suggestion_list_header, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case ListItemTypes.BUSINESS: {
                View itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.suggestion_list_item, parent, false);
                return new BusinessViewHolder(itemView);
            }
            default:
                return null;
        }
    }

    /**
     * @param vh
     * @param position This should not be `final` according to Yigit! https://youtu.be/imsr8NrIAMs?t=35m33s
     */
    @Override
    public void onBindViewHolder(final ViewHolder vh, int position) {
        switch (vh.getItemViewType()) {
            case ListItemTypes.HEADER: {
                HeaderViewHolder holder = (HeaderViewHolder) vh;
                final BusinessListHeader header = (BusinessListHeader) mBusinessList.get(position);
                mBusinessList.set(position, header);
                holder.mTextView_Header.setText(header.getKey());
            }
            break;

            case ListItemTypes.BUSINESS: {

                final BusinessViewHolder holder = (BusinessViewHolder) vh;

                // Get business at `position` index. This object's fields will be used to populate UI views
                final Business business = (Business) mBusinessList.get(position);

                // Business Header Layout - clickable
                holder.mLayout_BusinessHeader.setOnClickListener(onBusinessHeaderClick(business));

                // Business image
                if (!business.getImageUrl().isEmpty()) {
                    Picasso.with(parentView.getContext())
                            .load(business.getImageUrl())
                            .fit()
                            .into(holder.mImageView_BusinessImage);
                }

                // Business name
                holder.mTextView_BusinessName.setText(calculateCardPosition(position) + ". " + business.getName());

                holder.mImageView_BusinessRatingUrl
                        .setImageDrawable(parentView.getRatingDrawable(business.getRating()));

                // Business review count
                holder.mTextView_BusinessReviewCount.setText(business.getReviewCount() + " Reviews");

                holder.mTextView_BusinessCategories.setText(business.getFormattedCategories());

                holder.mTextView_BusinessAddress.setText(
                        String.format(
                                "%s, %s",
                                business.getLocation().getAddress1(),
                                business.getLocation().getCity()
                        )
                );

                // Dynamically add text based on UserRecords

                // Show descriptiveText , if present
                String descriptiveText = business.getDescriptiveText();
                if (descriptiveText != null) {
                    holder.mTextView_DescriptiveText.setVisibility(View.VISIBLE);
                    holder.mTextView_DescriptiveText.setText(descriptiveText);
                } else {
                    // If there's no descriptive text, hide the line separator and the layout
                    holder.mTextView_DescriptiveText.setVisibility(View.GONE);
                }

                // Like button dynamic icon
                if (business.isLiked()) {
                    holder.mButton_Like.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.thumb_up_fill, 0, 0);
                } else {
                    holder.mButton_Like.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.thumb_up_outline, 0, 0);
                }

                // Dont Like button dynamic icon
                if (business.isDontLike()) {
                    holder.mButton_DontLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.thumb_down_fill, 0, 0);
                } else {
                    holder.mButton_DontLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.thumb_down_outline, 0, 0);
                }

                // Click listeners
                holder.mButton_Like.setOnClickListener(onLikeClick(position, business));
                holder.mButton_TooSoon.setOnClickListener(onTooSoonClick(position));
                holder.mButton_DontLike.setOnClickListener(onDontLikeClick(position, business));
            }
            break;
        }
    }

    @NonNull
    private View.OnClickListener onLikeClick(int position, Business business) {
        return v -> {

            if (business.getDontLikeClickDate() != LIKED) {

                // TODO Get this out of the adapter
                // TODO Single source of truth
                // Update UserRecords
                mUserRecords.updateClickDate(business, LIKED, LIKE);

                // Update object field
                business.setDontLikeClickDate(LIKED);
                Timber.d("Updated dontLikeClickDate for " + business.getName() + " to "
                        + business.getDontLikeClickDate() + " position " + position);

                // UI stuff
                mBusinessList.remove(position);
                notifyItemRemoved(position);

                // Remove header if necessary
                // `position - 1` because you just removed an element
                removeHeader(position - 1);

                // Add business to the top of the Likes list
                //   Add a header if necessary (index 0)
                addHeader(LIKES_KEY);

                // Add the business one index after the header (index 1)
                mBusinessList.getSublist(LIKES_KEY).add(1, business);

                // Update RecyclerView item (triggers animation)
                notifyItemInserted(mBusinessList.getCombinedIndex(LIKES_KEY, 1));
                //notifyItemMoved(position, targetIndex);

                // Update other items in RecyclerView (this updates the item numbers in each CardView)
                // notifyItemRangeChanged() works with animations, as notifyDataSetChanged()
                // does not.
                // TODO Use payload to only update fields that have changed
                // For Likes, index 0 is the header so it doesn't need to change
                // Cards are inserted at the top, so index will always be 1 (unless this logic changes)
                notifyItemRangeChanged(1, getItemCount());

                // Notify user
                parentView.notifyUserBusinessLiked(business.getName());

            } else {
                // Unlike

                // TODO Get this out of the adapter
                // TODO Single source of truth
                // Update UserRecords
                mUserRecords.updateClickDate(business, 0, LIKE);

                // Update object field
                business.setDontLikeClickDate(0);
                Timber.d("Updated dontLikeClickDate for " + business.getName() + " to " + business.getDontLikeClickDate());

                notifyItemChanged(position);
            }

        };
    }

    @NonNull
    private View.OnClickListener onTooSoonClick(int position) {
        return v -> {
            // Get business and hold in temp variable
            Business businessTemp = (Business) mBusinessList.get(position);

            if (businessTemp.isDontLike()) {
                parentView.notifyNotAllowedOnDontLike();
            } else {
                // Remove existing element
                mBusinessList.remove(position);

                // Update RecyclerView item (triggers animation)
                notifyItemRemoved(position);

                // Remove header if necessary
                // `position - 1` because you just removed an element
                removeHeader(position - 1);

                // Which Too Soon list?
                String sublistKey = businessTemp.isLiked() ? LIKED_TOO_SOON_KEY : UNSORTED_TOO_SOON_KEY;

                //   Add a header if necessary
                if (mBusinessList.getSublist(sublistKey).isEmpty()) {
                    mBusinessList.getSublist(sublistKey).add(0, new BusinessListHeader(sublistKey));
                    int tooSoonStart = mBusinessList.getStartOfSublist(sublistKey);
                    notifyItemInserted(tooSoonStart);
                }

                // Add item to Too Soon sublist
                mBusinessList.getSublist(sublistKey).add(businessTemp); // How is this working???

                // Update other items in RecyclerView (this updates the item numbers in each CardView)
                // Start updating from which is higher on the list, the original position or the position it moved to
                int minIndexChanged = Math.min(mBusinessList.getCombinedIndex(
                        sublistKey,
                        mBusinessList
                                .getSublist(sublistKey)
                                .size() - 1), position);
                notifyItemRangeChanged(minIndexChanged, getItemCount());

                // Notify user
                parentView.notifyUserTooSoon(businessTemp.getName());

                // TODO Get this out of the adapter
                // TODO Single source of truth
                // Update user records
                long systemTime_ms = System.currentTimeMillis();
                mUserRecords.updateClickDate(businessTemp, systemTime_ms, TOOSOON);

                // Update object field
                businessTemp.setTooSoonClickDate(systemTime_ms);
                Timber.d("Updated tooSoonClickDate for " + businessTemp.getName() + " to " + systemTime_ms);
            }
        };
    }

    @NonNull
    private View.OnClickListener onDontLikeClick(int position, Business business) {
        return v -> {

            if (business.getDontLikeClickDate() <= 0) {

                // Get business and hold in temp variable
                Business businessTemp = (Business) mBusinessList.get(position);

                // Remove existing element
                mBusinessList.remove(position);

                // Update RecyclerView item (triggers animation)
                notifyItemRemoved(position);

                // Remove header if necessary
                // `position - 1` because you just removed an element
                removeHeader(position - 1);

                // Add header if necessary
                addHeader(DONT_LIKE_KEY);

                // Add business to bottom of Don't Like list
                mBusinessList.getSublist(DONT_LIKE_KEY).add(businessTemp);

                // Update other items in RecyclerView (this updates the item numbers in each CardView)
                // Since Don't Likes go to the bottom, the range will always be from the original `position` downwards
                notifyItemRangeChanged(position, getItemCount());

                // Notify user
                parentView.notifyUserBusinessDontLiked(business.getName());

                // Get current date/time
                long systemTime_ms = System.currentTimeMillis();

                // TODO Get this out of the adapter
                // TODO Single source of truth
                // Update user records
                mUserRecords.updateClickDate(businessTemp, systemTime_ms, DONTLIKE);

                // Update object field
                businessTemp.setDontLikeClickDate(systemTime_ms);
                Timber.d("Updated dontLikeClickDate for " + businessTemp.getName() + " to " + systemTime_ms);

            } else {
                // Un-Don't Like

                // TODO Get this out of the adapter
                // Update UserRecords
                mUserRecords.updateClickDate(business, 0, DONTLIKE);

                // Update object field
                business.setDontLikeClickDate(0);
                Timber.d("Updated dontLikeClickDate for " + business.getName() + " to " + business.getDontLikeClickDate());

                notifyItemChanged(position);
            }
        };
    }

    @NonNull
    private View.OnClickListener onBusinessHeaderClick(Business business) {
        return v -> parentView.launchBusinessUrl(business.getUrl());
    }

    void dismiss(final int position) {

        // UI Stuff:
        // Get business and hold in temp variable
        final Business business = (Business) mBusinessList.get(position);

        // Remove existing element
        mBusinessList.remove(position);

        // Update RecyclerView item (triggers animation)
        notifyItemRemoved(position);

        // Remove header if necessary
        // position - 1 because you just removed one
        removeHeader(position - 1);

        // Update other items in RecyclerView (this updates the item numbers in each CardView)
        notifyItemRangeChanged(position, getItemCount());

        // Notify user
        parentView.notifyUserBusinessDismissed(position, business);

        // TODO Get this out of the adapter
        // Update user records
        mUserRecords.incrementDismissedCount(business.getId());
    }

    /**
     * @param position Card's index in the list
     * @return Business's number in list (subtracts headers)
     */
    private int calculateCardPosition(final int position) {
        int headerCount = 0;
        for (int i = 0; i < position; i++) {
            if (mBusinessList.get(i) instanceof BusinessListHeader) headerCount++;
        }
        return position + 1 - headerCount;
    }

    private void addHeader(final String key) {
        if (mBusinessList.getSublist(key).isEmpty()) {
            // Headers will always be added at index 0
            mBusinessList.getSublist(key).add(0, new BusinessListHeader(key));
            int index = mBusinessList.getStartOfSublist(key);
            notifyItemInserted(index);
        }
    }

    private void removeHeader(int position) {
        for (int i = position; i >= 0; i--) {
            if (mBusinessList.get(i) instanceof BusinessListHeader) {
                String key = ((BusinessListHeader) mBusinessList.get(i)).getKey();
                if (mBusinessList.getSublist(key).size() < 2) {
                    mBusinessList.remove(i);
                    notifyItemRemoved(i);
                    break; // Only do this for the first header
                }
            }
        }
    }

    void undoDismiss(final int position, final Business business) {
        mBusinessList.add(position, business);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());

        if (position == 0) {
            mLayoutManager.scrollToPosition(0);
        }
    }

    @Override
    public int getItemCount() {
        if (mBusinessList == null) {
            return 0;
        } else {
            return mBusinessList.size();
        }
    }

    void setBusinessList(final CombinedList businesses) {
        this.mBusinessList = businesses;
    }

    CombinedList getBusinessList() {
        return mBusinessList;
    }
}
