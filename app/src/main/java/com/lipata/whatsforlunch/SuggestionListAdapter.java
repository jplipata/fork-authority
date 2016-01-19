package com.lipata.whatsforlunch;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipata.whatsforlunch.data.yelppojo.Business;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jlipata on 1/1/16.
 */
public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.ViewHolder> {

    static public String LOG_TAG = SuggestionListAdapter.class.getSimpleName();
    private List<Business> mBusinessList;
    private Context mContext;
    private CoordinatorLayout mCoordinatorLayout;

    public SuggestionListAdapter(List<Business> businessList, Context context, CoordinatorLayout coordinatorLayout){
        this.mBusinessList = businessList;
        this.mContext = context;
        this.mCoordinatorLayout = coordinatorLayout;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView_BusinessImage;
        public TextView mTextView_BusinessName;
        public TextView mTextView_BusinessCategories;
        public TextView mTextView_BusinessAddress;
        public ImageView mImageView_BusinessRatingUrl;
        public TextView mTextView_BusinessReviewCount;
        public Button mButton_TooSoon;
        public Button mButton_Like;
        public Button mButton_DontLike;

        public ViewHolder(View v) {
            super(v);
            mImageView_BusinessImage = (ImageView) v.findViewById(R.id.business_image);
            mTextView_BusinessName = (TextView) v.findViewById(R.id.business_name);
            mTextView_BusinessCategories = (TextView) v.findViewById(R.id.business_categories);
            mTextView_BusinessAddress = (TextView) v.findViewById(R.id.business_address);
            mImageView_BusinessRatingUrl = (ImageView) v.findViewById(R.id.business_rating);
            mTextView_BusinessReviewCount = (TextView) v.findViewById(R.id.business_review_count);
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
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get business at `position` index. This object's fields will be used to populate UI views
        final Business business = mBusinessList.get(position);

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
                Snackbar.make(mCoordinatorLayout,
                        "Noted. You just ate at " + business.getName() + ". I won't suggest this again for a couple days.",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        holder.mButton_DontLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mCoordinatorLayout,
                        "Noted. You don't like " + business.getName() + ". I won't suggest this again for some time.",
                        Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBusinessList.size();
    }
}
