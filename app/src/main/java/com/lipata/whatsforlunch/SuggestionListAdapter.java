package com.lipata.whatsforlunch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lipata.whatsforlunch.data.yelppojo.Business;

import java.util.List;

/**
 * Created by jlipata on 1/1/16.
 */
public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.ViewHolder> {

    private List<Business> businessList;

    public SuggestionListAdapter(List<Business> businessList){
        this.businessList = businessList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView_BusinessName;
        public TextView mTextView_BusinessPhone;
        public TextView mTextView_BusinessUrl;

        public ViewHolder(View v) {
            super(v);
            mTextView_BusinessName = (TextView) v.findViewById(R.id.business_name);
            mTextView_BusinessPhone = (TextView) v.findViewById(R.id.business_phone);
            mTextView_BusinessUrl = (TextView) v.findViewById(R.id.business_url);
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
        Business business = businessList.get(position);
        holder.mTextView_BusinessName.setText(business.getName());
        holder.mTextView_BusinessPhone.setText(business.getPhone());
        holder.mTextView_BusinessUrl.setText(business.getUrl());
    }


    @Override
    public int getItemCount() {
        return businessList.size();
    }
}
