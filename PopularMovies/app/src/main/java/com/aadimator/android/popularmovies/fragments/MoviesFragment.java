package com.aadimator.android.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aadimator.android.popularmovies.R;
import com.aadimator.android.popularmovies.activities.MainActivity;
import com.aadimator.android.popularmovies.activities.SettingsActivity;
import com.aadimator.android.popularmovies.adapters.MovieAdapter;
import com.aadimator.android.popularmovies.data.MovieContract;
import com.aadimator.android.popularmovies.helpers.QueryUtils;
import com.aadimator.android.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aadam on 10/11/2016.
 * <p>
 * Displays a grid of movie posters
 */

public class MoviesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    public static final int MOVIE_LOADER_ID = 1;
    // Adapter for the list of movies
    private MovieAdapter mMovieAdapter;
    GridView mGridView;
    private RelativeLayout mEmptyView;
    private TextView mEmptyTitle;
    private TextView mEmptySubtitle;
    private boolean mShowFavorites;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailsFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Movie movie);
    }

    public MoviesFragment() {

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mEmptyView = (RelativeLayout) rootView.findViewById(R.id.empty_view);
        mEmptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title_text);
        mEmptySubtitle = (TextView) mEmptyView.findViewById(R.id.empty_subtitle_text);

        mGridView.setEmptyView(mEmptyView);

        // Create a new adapter that takes an empty list of movies as input
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie currentMovie = (Movie) parent.getItemAtPosition(position);
                if (currentMovie != null) {
                    ((Callback) getActivity()).onItemSelected(currentMovie);
                }
            }
        });

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query setting
        preferences.registerOnSharedPreferenceChangeListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // Check if Internet is Available
        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            Toast.makeText(getActivity(), R.string.connectionFailureMsg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        return new MovieLoader(getActivity(), QueryUtils.createMoviesUrlPath(getContext()), mShowFavorites);
    }

    public static String getSortingPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default)
        );
    }


    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data != null) {
            if (data.isEmpty() && mShowFavorites) {
                mEmptyTitle.setText(R.string.empty_title_no_fav);
                mEmptySubtitle.setText(R.string.empty_subtitle_no_fav);
            }
            mMovieAdapter.clear();
            mMovieAdapter.addAll(data);
        }

        if (MainActivity.mTwoPane) {
            if (mMovieAdapter.getCount() > 0) {
                final Movie currentMovie = (Movie) mMovieAdapter.getItem(0);
                if (currentMovie != null) {
                    mGridView.post(new Runnable() {
                        @Override
                        public void run() {
                            ((Callback) getActivity()).onItemSelected(currentMovie);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        mMovieAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_by_key))) {
            // Clear the GridView as a new query will be kicked off
            mMovieAdapter.clear();

            // Restart the loader
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
            startActivityForResult(settingsIntent, 0);
            return true;
        } else if (id == R.id.action_show_fav) {
            mShowFavorites = !mShowFavorites;
            item.setChecked(mShowFavorites);

            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_favorite);
            } else {
                item.setIcon(R.drawable.ic_favorite_border);
            }

            getLoaderManager().restartLoader(MoviesFragment.MOVIE_LOADER_ID, null, this);
        }
        return super.onOptionsItemSelected(item);
    }
}

class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieLoader.class.getSimpleName();

    /**
     * Query URL
     */
    private String mUrl;
    private List<Movie> mData;
    private boolean mShowFavorites;

    public MovieLoader(Context context, String url, boolean showFavs) {
        super(context);
        mShowFavorites = showFavs;
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

        if (mShowFavorites) {
            Log.i(LOG_TAG, "Fetching Movies from Database");
            Cursor cursor = getContext().getContentResolver().
                    query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
            return QueryUtils.fetchMoviesDataFromDatabase(cursor);
        }

        // Perform the network request, parse the response, and extract a list of movies.
        return QueryUtils.fetchMoviesData(mUrl, getContext());
    }

    @Override
    public void deliverResult(List<Movie> data) {
        // We'll save the data for later retrieval
        mData = data;
        super.deliverResult(data);
    }
}

