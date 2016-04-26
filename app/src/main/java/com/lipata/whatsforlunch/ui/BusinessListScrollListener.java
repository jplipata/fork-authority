package com.lipata.whatsforlunch.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.lipata.whatsforlunch.api.TestApiCall;

/**
 * Created by jlipata on 4/23/16.
 */
public class BusinessListScrollListener extends RecyclerView.OnScrollListener {
    private final static String LOG_TAG = BusinessListScrollListener.class.getSimpleName();

    LinearLayoutManager mLayoutManager;
    TestApiCall mApiCall; //TODO This is probably not a good way to do this
    int visibleItemCount;
    int totalItemCount;
    int firstVisibleItemPosition;
    int lastVisibleItemPosition;



    public BusinessListScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        mApiCall = new TestApiCall();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //Log.d(LOG_TAG, "dx: "+dx+" dy: "+dy);

         visibleItemCount = mLayoutManager.getChildCount();
         totalItemCount = mLayoutManager.getItemCount();
         firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
         lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

        Log.d(LOG_TAG, "visibleItemCount "+visibleItemCount+" | totalItemCount "+totalItemCount
                +" | firstVisibleItemPosition "+firstVisibleItemPosition+" | lastVisibleItemPosition "+lastVisibleItemPosition);

        if(lastVisibleItemPosition+1 == totalItemCount) {
            if (mApiCall.isInProgress() == false) {
                Log.d(LOG_TAG, "LOAD MORE ITEMS");

            } else {
                Log.d(LOG_TAG, "LOADING IN PROGRESS");
            }
        }


    }
}
