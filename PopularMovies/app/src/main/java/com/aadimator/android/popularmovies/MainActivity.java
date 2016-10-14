package com.aadimator.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 1;
    // Adapter for the list of movies
    private MovieAdapter mMovieAdapter;

    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMG_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String DEFAULT_IMG_SIZE = "w185";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setEmptyView(findViewById(R.id.empty_view));

        // Create a new adapter that takes an empty list of movies as input
        mMovieAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        gridView.setAdapter(mMovieAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query setting
        preferences.registerOnSharedPreferenceChangeListener(this);

        // Check if Internet is Available
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        } else {
            Toast.makeText(MainActivity.this, R.string.connectionFailureMsg, Toast.LENGTH_LONG).show();
        }

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
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String api_key = "5b449f77e39ea99dc845544707c12fbb";
        String page = "1";
        String lang = "en-US";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = sharedPreferences.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default)
        );

        uriBuilder.appendPath(sortBy);

        uriBuilder.appendQueryParameter("api_key", api_key);
        uriBuilder.appendQueryParameter("language", lang);
        uriBuilder.appendQueryParameter("page", page);

        Log.d(LOG_TAG, "URL : " + uriBuilder.toString());

        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data != null && !data.isEmpty()) {
            // remove previous data
            mMovieAdapter.clear();
            mMovieAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        mMovieAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
