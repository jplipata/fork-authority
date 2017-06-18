package com.lipata.forkauthority.ui;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;

import com.lipata.forkauthority.data.ListRanker;

/**
 * Created by jlipata on 3/19/17.
 */

public interface MainView {
    void updateLocationViews(double latitude, double longitude, int accuracyQuality);

    void startRefreshAnimation();

    void stopRefreshAnimation();

    void showSnackBarIndefinite(String text);

    void showToast(String text);

    void setLocationText(String text);

    void onDeviceLocationRequested();

    void onDeviceLocationRetrieved();

    void onNewBusinessListReceived();

    // Trigger location + yelp calls
    void fetchBusinessList();

    void logFabricAnswersMetric(String metricName, long startTime);

    //TODO RecyclerView.LayoutManager has been replaced by android.support.v7.widget.LinearLayoutManager.  For some reason this still works, but it could cause problems later.
    RecyclerView.LayoutManager getRecyclerViewLayoutManager();

    CoordinatorLayout getCoordinatorLayout();

    BusinessListAdapter getSuggestionListAdapter();

    ListRanker getListRanker();

    boolean isNetworkConnected();
}
