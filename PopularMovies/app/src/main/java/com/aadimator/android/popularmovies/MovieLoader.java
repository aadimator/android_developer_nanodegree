package com.aadimator.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Aadam on 10/9/2016.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieLoader.class.getSimpleName();

    /**
     * Query URL
     */
    private String mUrl;
    private List<Movie> mData;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Use cached data
            deliverResult(mData);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of movies.
        List<Movie> movieList = QueryUtils.fetchMoviesData(mUrl);
        return movieList;
    }

    @Override
    public void deliverResult(List<Movie> data) {
        // We'll save the data for later retrieval
        mData = data;
        super.deliverResult(data);
    }
}
