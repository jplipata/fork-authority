package com.lipata.forkauthority.data;

import com.lipata.forkauthority.api.yelp3.TokenManager;
import com.lipata.forkauthority.api.yelp3.Yelp3ApiAuthInterceptor;
import com.lipata.forkauthority.api.yelp3.Yelp3ApiClient;
import com.lipata.forkauthority.api.yelp3.entities.Business;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.observers.TestObserver;

public class ListFetcherTest {
    private static final String LATITUDE = "40.722091";
    private static final String LONGITUDE = "-73.843692";

    private Yelp3ApiClient api;
    private TokenManager tokenManager;

    @Before
    public void setUp() throws Exception {
        api = new Yelp3ApiClient(new Yelp3ApiAuthInterceptor());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getList() throws Exception {
        TestObserver<List<Business>> testObserver = TestObserver.create();

        ListFetcher listFetcher = new ListFetcher(api);
        listFetcher
                .getList(LATITUDE, LONGITUDE)
                .subscribe(testObserver);

        testObserver.assertSubscribed();
    }

}