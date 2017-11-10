package com.lipata.forkauthority.data;

import android.util.Log;

import com.lipata.forkauthority.Util.Utility;
import com.lipata.forkauthority.api.yelp3.TokenManager;
import com.lipata.forkauthority.api.yelp3.Yelp3Api;
import com.lipata.forkauthority.api.yelp3.Yelp3ApiClient;
import com.lipata.forkauthority.api.yelp3.entities.Business;
import com.lipata.forkauthority.api.yelp3.entities.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

import static com.lipata.forkauthority.data.AppSettings.MAX_API_CALLS;

public class ListFetcher {
    private static final String LOG_TAG = ListFetcher.class.getSimpleName();

    private final Yelp3ApiClient api;
    private final TokenManager tokenManager;

    @Inject
    ListFetcher(final Yelp3ApiClient api, final TokenManager tokenManager) {
        this.api = api;
        this.tokenManager = tokenManager;
    }

    /**
     * Main call to retrieve list of businesses, makes initial call to determine how many businesses
     * there are in total, then if necessary, will call subsequentSearchCalls() to retrieve the
     * remaining
     * @param latitude
     * @param longitude
     * @return
     */
    public Single<List<Business>> getList(final String latitude, final String longitude) {
        Log.d(LOG_TAG, "getList() ");
        return api
                .search(
                        tokenManager.getToken(),
                        AppSettings.SEARCH_TERM,
                        latitude,
                        longitude,
                        AppSettings.SEARCH_RADIUS,
                        Yelp3Api.SEARCH_LIMIT)
                .compose(Utility::applySchedulers)
                .flatMap(searchResponse -> {
                    if (searchResponse.getBusinesses().size() < searchResponse.getTotal()) {
                        return subsequentSearchCalls(searchResponse, latitude, longitude)
                                .map(businesses -> {
                                    List<Business> list = new ArrayList<>();
                                    list.addAll(searchResponse.getBusinesses());
                                    list.addAll(businesses);
                                    return list;
                                });
                    } else {
                        return Single.just(searchResponse.getBusinesses());
                    }
                });
    }

    private Single<List<Business>> subsequentSearchCalls(
            final SearchResponse searchResponse,
            final String latitude,
            final String longitude) {
        double numberOfCalls =
                (double) (searchResponse.getTotal() - searchResponse.getBusinesses().size()) / Yelp3Api.SEARCH_LIMIT;

        Log.d(LOG_TAG, "searchResponse.getTotal() " + searchResponse.getTotal());
        Log.d(LOG_TAG, "searchResponse.getBusinesses().size() " + searchResponse.getBusinesses().size());
        Log.d(LOG_TAG, "numberOfCalls " + (int) Math.ceil(numberOfCalls));

        return Observable
                .range(1, (int) Math.ceil(numberOfCalls)) // call offset, always round up
                .take(MAX_API_CALLS - 1)
                .map(integer -> integer * Yelp3Api.SEARCH_LIMIT)
                .flatMap(integer -> api
                        .search(
                                tokenManager.getToken(),
                                AppSettings.SEARCH_TERM,
                                latitude,
                                longitude,
                                AppSettings.SEARCH_RADIUS,
                                Yelp3Api.SEARCH_LIMIT,
                                integer)
                        .compose(Utility::applySchedulers)
                        .toObservable())
                .map(SearchResponse::getBusinesses)
                .reduce((businesses, businesses2) -> {
                    List<Business> list = new ArrayList<>();
                    list.addAll(businesses);
                    list.addAll(businesses2);
                    return list;
                }).toSingle();
    }
}


