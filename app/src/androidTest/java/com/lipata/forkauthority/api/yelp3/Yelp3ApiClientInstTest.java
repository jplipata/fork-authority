package com.lipata.forkauthority.api.yelp3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lipata.forkauthority.R;
import com.lipata.forkauthority.api.yelp3.entities.SearchResponse;
import com.lipata.forkauthority.data.AppSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.TestObserver;

/**
 * Created by jlipata on 11/25/17.
 */
@RunWith(AndroidJUnit4.class)
public class Yelp3ApiClientInstTest {
    private static final String LATITUDE = "40.722091";
    private static final String LONGITUDE = "-73.843692";

    private Yelp3ApiClient api;
    private TokenManager tokenManager;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        tokenManager = new TokenManager(
                context,
                context.getSharedPreferences(
                        context.getString(R.string.test_shared_prefs_file), // Using a test sharedprefs file
                        Context.MODE_PRIVATE)
        );

        api = new Yelp3ApiClient(
                new Yelp3ApiAuthInterceptor(tokenManager),
                new Yelp3ApiAuthenticator(tokenManager));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAuthenticator() throws Exception {
        // Erase the token
        tokenManager.setSharedPrefToken(null);

        // Make an api call sans token
        TestObserver<SearchResponse> testObserver = TestObserver.create();
        api
                .search(
                        AppSettings.SEARCH_TERM,
                        LATITUDE,
                        LONGITUDE,
                        AppSettings.SEARCH_RADIUS,
                        Yelp3Api.SEARCH_LIMIT
                )
                .subscribe(testObserver);

        // Make sure we got a response with businesses
        testObserver.assertValue(searchResponse -> {
            return searchResponse.getBusinesses().size() > 0;
        });

        testObserver.assertNoErrors();
        testObserver.assertSubscribed();
    }
}