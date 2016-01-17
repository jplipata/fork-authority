package com.lipata.whatsforlunch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public SuggestionListAdapter(List<Business> businessList, Context context){
        this.mBusinessList = businessList;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView_BusinessImage;
        public TextView mTextView_BusinessName;
        public TextView mTextView_BusinessCategories;
        public TextView mTextView_BusinessAddress;
        public ImageView mImageView_BusinessRatingUrl;
        public TextView mTextView_BusinessReviewCount;


        public ViewHolder(View v) {
            super(v);
            mImageView_BusinessImage = (ImageView) v.findViewById(R.id.business_image);
            mTextView_BusinessName = (TextView) v.findViewById(R.id.business_name);
            mTextView_BusinessCategories = (TextView) v.findViewById(R.id.business_categories);
            mTextView_BusinessAddress = (TextView) v.findViewById(R.id.business_address);
            mImageView_BusinessRatingUrl = (ImageView) v.findViewById(R.id.business_rating);
            mTextView_BusinessReviewCount = (TextView) v.findViewById(R.id.business_review_count);

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
        Business business = mBusinessList.get(position);

        // Business image
        Picasso.with(mContext)
                .load(business.getImageUrl()).fit()
                .into(holder.mImageView_BusinessImage);

        // Business name
        holder.mTextView_BusinessName.setText(position + 1 + ". " + business.getName());

        // Business rating image
        Picasso.with(mContext)
                .load(business.getRatingImgUrlLarge()).resize(440,60).centerInside()
                .into(holder.mImageView_BusinessRatingUrl);

        // Business review count
        holder.mTextView_BusinessReviewCount.setText(business.getReviewCount() + " Reviews");

        //List<T> categoryList = business.getCategories();

        //holder.mTextView_BusinessCategories.setText(formattedCategories);

        holder.mTextView_BusinessAddress.setText(business.getLocation().getFormattedDisplayAddress());
    }

    @Override
    public int getItemCount() {
        return mBusinessList.size();
    }
}
