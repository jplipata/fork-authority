package com.lipata.forkauthority.data;

import com.lipata.forkauthority.BuildConfig;
import com.lipata.forkauthority.api.yelp3.TokenManager;
import com.lipata.forkauthority.api.yelp3.Yelp3Api;
import com.lipata.forkauthority.api.yelp3.Yelp3ApiAuthInterceptor;
import com.lipata.forkauthority.api.yelp3.Yelp3ApiAuthenticator;
import com.lipata.forkauthority.api.yelp3.Yelp3ApiClient;
import com.lipata.forkauthority.api.yelp3.entities.Business;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import io.reactivex.observers.TestObserver;

public class ListFetcherTest {

    private final static String TEST_TOKEN_STRING =
            String.format(Yelp3Api.AUTH_FORMAT, BuildConfig.YELPFUSION_TEST_TOKEN);
    private static final String LATITUDE = "40.722091";
    private static final String LONGITUDE = "-73.843692";

    private Yelp3ApiClient api;
    private TokenManager tokenManager;

    @Before
    public void setUp() throws Exception {
        tokenManager = Mockito.mock(TokenManager.class);
        Mockito.when(tokenManager.getToken()).thenReturn(TEST_TOKEN_STRING);
        api = new Yelp3ApiClient(
                new Yelp3ApiAuthInterceptor(tokenManager),
                new Yelp3ApiAuthenticator(tokenManager));
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