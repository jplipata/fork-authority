package com.lipata.forkauthority.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

/**
 * Created by jlipata on 4/23/16.
 */
public class BusinessListScrollListener extends RecyclerView.OnScrollListener {
    private final static String LOG_TAG = BusinessListScrollListener.class.getSimpleName();

    LinearLayoutManager mLayoutManager;
    int visibleItemCount;
    int totalItemCount;
    int firstVisibleItemPosition;
    int lastVisibleItemPosition;

    public BusinessListScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //Log.d(LOG_TAG, "dx: "+dx+" dy: "+dy);

         visibleItemCount = mLayoutManager.getChildCount();
         totalItemCount = mLayoutManager.getItemCount();
         firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
         lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

        Log.v(LOG_TAG, "visibleItemCount "+visibleItemCount+" | totalItemCount "+totalItemCount
                +" | firstVisibleItemPosition "+firstVisibleItemPosition+" | lastVisibleItemPosition "+lastVisibleItemPosition);

        if(lastVisibleItemPosition+1 == totalItemCount) {

            Log.v(LOG_TAG, "Reached bottom of list");

            // Load more items

        }

    }
}
