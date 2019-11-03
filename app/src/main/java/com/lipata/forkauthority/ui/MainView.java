package com.lipata.forkauthority.ui;

import com.lipata.forkauthority.api.yelp3.entities.Business;

public interface MainView {
    void updateLocationViews(double latitude, double longitude, int accuracyQuality);

    void startRefreshAnimation();

    void stopRefreshAnimation();

    void showSnackBarIndefinite(String text);

    void showSnackBarLong(String text);

    void showSnackBarLongWithAction(String message, String actionLabel, int position, Business business);

    void showToast(String text);

    void setLocationText(String text);

    void onDeviceLocationRequested();

    void onDeviceLocationRetrieved();

    void onNewBusinessListReceived();

    // Trigger location + yelp calls
    void fetchBusinessList();

    void logFabricAnswersMetric(String metricName, long startTime);

    BusinessListAdapter getSuggestionListAdapter();

    boolean isNetworkConnected();

    void onNoResults();

}
