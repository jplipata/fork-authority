package com.lipata.whatsforlunch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lipata.whatsforlunch.data.user.UserRecords;
import com.lipata.whatsforlunch.data.yelppojo.Business;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by jlipata on 1/1/16.
 */
public class BusinessListAdapter extends RecyclerView.Adapter<BusinessListAdapter.ViewHolder> {

    // Button ID constants
    public static final int LIKE = 0;
    public static final int TOOSOON = 1;
    public static final int DONTLIKE = 2;
    public static final int DISMISS = 3;

    static public String LOG_TAG = BusinessListAdapter.class.getSimpleName();
    private List<Business> mBusinessList;
    private Context mContext;
    private CoordinatorLayout mCoordinatorLayout;
    UserRecords mUserRecords;
    BusinessListFilter mBusinessListFilter;

    public BusinessListAdapter(Context context, CoordinatorLayout coordinatorLayout,
                               UserRecords userRecords, BusinessListFilter businessListFilter){
        this.mContext = context;
        this.mCoordinatorLayout = coordinatorLayout;
        this.mUserRecords = userRecords;
        this.mBusinessListFilter = businessListFilter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mLayout_BusinessHeader;
        public CardView mCardView_CardView;
        public ImageView mImageView_BusinessImage;
        public TextView mTextView_BusinessName;
        public TextView mTextView_BusinessCategories;
        public TextView mTextView_BusinessAddress;
        public ImageView mImageView_BusinessRatingUrl;
        public TextView mTextView_BusinessReviewCount;
        public TextView mTextView_JustAteHereDate;
        public TextView mTextView_LikeDontLike;
        public Button mButton_TooSoon;
        public Button mButton_Like;
        public Button mButton_DontLike;

        public ViewHolder(View v) {
            super(v);
            mLayout_BusinessHeader = (LinearLayout) v.findViewById(R.id.business_header_layout);
            mCardView_CardView = (CardView) v.findViewById(R.id.card_view);
            mImageView_BusinessImage = (ImageView) v.findViewById(R.id.business_image);
            mTextView_BusinessName = (TextView) v.findViewById(R.id.business_name);
            mTextView_BusinessCategories = (TextView) v.findViewById(R.id.business_categories);
            mTextView_BusinessAddress = (TextView) v.findViewById(R.id.business_address);
            mImageView_BusinessRatingUrl = (ImageView) v.findViewById(R.id.business_rating);
            mTextView_BusinessReviewCount = (TextView) v.findViewById(R.id.business_review_count);
            mTextView_JustAteHereDate = (TextView) v.findViewById(R.id.business_justateheredate);
            mTextView_LikeDontLike = (TextView) v.findViewById(R.id.business_likeordontlike);
            mButton_TooSoon = (Button) v.findViewById(R.id.button_toosoon);
            mButton_Like = (Button) v.findViewById(R.id.button_like);
            mButton_DontLike = (Button) v.findViewById(R.id.button_dontlike);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // Get business at `position` index. This object's fields will be used to populate UI views
        final Business business = mBusinessList.get(position);

        // Business Header Layout - clickable
        holder.mLayout_BusinessHeader.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(business.getUrl()));
                mContext.startActivity(browserIntent);
            }
        });

        // Business image
        Picasso.with(mContext)
                .load(business.getImageUrl()).fit()
                .into(holder.mImageView_BusinessImage);

        // Business name
        holder.mTextView_BusinessName.setText(position + 1 + ". " + business.getName());

        // Business rating image
        Picasso.with(mContext)
                .load(business.getRatingImgUrlLarge()).fit()
                .into(holder.mImageView_BusinessRatingUrl);

        // Business review count
        holder.mTextView_BusinessReviewCount.setText(business.getReviewCount() + " Reviews");

        StringBuilder stringBuilder = new StringBuilder();
        List<List<String>> categoryList = business.getCategories();
        for(int i=0; i<categoryList.size(); i++){
            String category = categoryList.get(i).get(0);
            stringBuilder.append(category);
            if(i<(categoryList.size()-1)){
                stringBuilder.append(", ");
            }
        }
        String formattedCategories = stringBuilder.toString();

        holder.mTextView_BusinessCategories.setText(formattedCategories);

        holder.mTextView_BusinessAddress.setText(business.getLocation().getFormattedDisplayAddress());

        // Dynamically add text based on UserRecords
        long tooSoonClickDate = business.getTooSoonClickDate();
        if(tooSoonClickDate!=0) {
            //Log.d(LOG_TAG, business.getName()+" has a tooSoonClickDate");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(business.getTooSoonClickDate());
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DATE);
            int year = calendar.get(Calendar.YEAR);
            holder.mTextView_JustAteHereDate.setVisibility(View.VISIBLE);
            holder.mTextView_JustAteHereDate.setText("Just ate here on "+month+"/"+day+"/"+year);
        } else {
            //Log.d(LOG_TAG, business.getName()+" does not have a tooSoonClickDate");
            holder.mTextView_JustAteHereDate.setVisibility(View.GONE);
        }

        long dontlikeClickDate = business.getDontLikeClickDate();
        if(dontlikeClickDate!=0){
            holder.mTextView_LikeDontLike.setVisibility(View.VISIBLE);
            holder.mTextView_LikeDontLike.setText("Don't like");
        } else {
            holder.mTextView_LikeDontLike.setVisibility(View.GONE);
        }

        holder.mButton_Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mCoordinatorLayout,
                        "Noted. You like " + business.getName() + ". I will suggest this more often.",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        holder.mButton_TooSoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(LOG_TAG, "Too Soon Clicked");

                // Get current date/time
                long systemTime_ms = System.currentTimeMillis();

                // Update user records
                mUserRecords.updateClickDate(business, systemTime_ms, TOOSOON);
                mUserRecords.commit();

                // Update object field
                business.setTooSoonClickDate(systemTime_ms);
                Log.d(LOG_TAG, "Updated tooSoonClickDate for " + business.getName() + " to " + systemTime_ms);

                // Update current list, move item down the list
                mBusinessList = mBusinessListFilter.moveItemToBottom(mBusinessList, mBusinessList.indexOf(business)); // This returns a list with null values
                mBusinessList.removeAll(Collections.singleton(null)); // Remove nulls

                // Notify user
                Snackbar.make(mCoordinatorLayout,
                        "Noted. You just ate at " + business.getName() + ". I have moved this to the bottom of the list.",
                        Snackbar.LENGTH_LONG).show();

                // Update Suggestion List View
                notifyDataSetChanged();
            }
        });

        holder.mButton_DontLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get current date/time
                long systemTime_ms = System.currentTimeMillis();

                // Update user records
                mUserRecords.updateClickDate(business, systemTime_ms, DONTLIKE);
                mUserRecords.commit();

                // Update object field
                business.setDontLikeClickDate(systemTime_ms) ;
                Log.d(LOG_TAG, "Updated dontLikeClickDate for " + business.getName() + " to " + systemTime_ms);

                // Update current list, move item down the list
                mBusinessList = mBusinessListFilter.moveItemToBottom(mBusinessList, mBusinessList.indexOf(business)); // This returns a list with null values
                mBusinessList.removeAll(Collections.singleton(null)); // Remove nulls

                // Notify user
                Snackbar.make(mCoordinatorLayout,
                        "Noted. You don't like " + business.getName() + ". I won't suggest this again for some time.",
                        Snackbar.LENGTH_LONG).show();

                // Update Suggestion List View
                notifyDataSetChanged();
            }
        });

    }

    public void setBusinessList(List<Business> businesses){
        this.mBusinessList = businesses;
    }

    @Override
    public int getItemCount() {
        if(mBusinessList==null){
            return 0;
        } else {
            return mBusinessList.size();
        }
    }

    public void dismiss(int position){

        // UI Stuff:
        // Get business and hold in temp variable
        Business business = mBusinessList.get(position);

        // Remove existing element
        mBusinessList.remove(position);

        // Update RecyclerView item (triggers animation)
        notifyItemRemoved(position);

        // Add business to bottom of list
        mBusinessList.add(business);

        // Update other items in RecyclerView (this updates the item numbers in each CardView)
        notifyItemRangeChanged(position, getItemCount());

        // Backend stuff:
        // Get current date/time
        long systemTime_ms = System.currentTimeMillis();

        // Update user records
        mUserRecords.updateClickDate(business, systemTime_ms, DISMISS);
        mUserRecords.commit();

        // Update object field
        business.setDismissedDate(systemTime_ms); ;
        Log.d(LOG_TAG, "Updated dismissedDate for " + business.getName() + " to " + systemTime_ms);

    }


}
