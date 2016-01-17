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
        public TextView mTextView_BusinessName;
        public TextView mTextView_BusinessCategories;
        public TextView mTextView_BusinessAddress;
        public ImageView mImageView_BusinessRatingUrl;

        public ViewHolder(View v) {
            super(v);
            mTextView_BusinessName = (TextView) v.findViewById(R.id.business_name);
            mTextView_BusinessCategories = (TextView) v.findViewById(R.id.business_categories);
            mTextView_BusinessAddress = (TextView) v.findViewById(R.id.business_address);
            mImageView_BusinessRatingUrl = (ImageView) v.findViewById(R.id.business_rating);
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
        Business business = mBusinessList.get(position);

        holder.mTextView_BusinessName.setText(position + 1 + ". " + business.getName());
        Picasso.with(mContext)
                .load(business.getRatingImgUrlLarge()).resize(440,60).centerInside()
                .into(holder.mImageView_BusinessRatingUrl);
        List<T> categoryList = business.getCategories();
        
        holder.mTextView_BusinessCategories.setText(formattedCategories);
        holder.mTextView_BusinessAddress.setText(business.getLocation().getFormattedDisplayAddress());
    }

    @Override
    public int getItemCount() {
        return mBusinessList.size();
    }
}
