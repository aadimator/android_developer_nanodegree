package com.udacity.gradle.builditbigger;

import android.test.AndroidTestCase;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.util.concurrent.ExecutionException;

/**
 * Created by Aadam on 10/16/2016.
 */

public class TellJokeTaskTest extends AndroidTestCase {

    private static final String LOG_TAG = TellJokeTask.class.getSimpleName();

    public void testFetchesNonEmptyString() {
        TellJokeTask tellJokeTask = new TellJokeTask();
        tellJokeTask.execute();
        try {
            String joke = tellJokeTask.get();
            Log.d(LOG_TAG, "Joke : " + joke);
            assertNotNull(joke);
            assertTrue(joke.length() > 0);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
    }
}
