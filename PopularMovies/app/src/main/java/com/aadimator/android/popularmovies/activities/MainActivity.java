package com.aadimator.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.aadimator.android.popularmovies.R;
import com.aadimator.android.popularmovies.fragments.DetailsFragment;
import com.aadimator.android.popularmovies.fragments.MoviesFragment;
import com.aadimator.android.popularmovies.models.Movie;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "DF_TAG";
    public static final String MOVIE_KEY = "currentMovie";
    public static boolean mTwoPane = false;

    public static final String TWO_PANE_KEY = "twoPane";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailsFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();

                getSupportActionBar().setElevation(0);
            } else {
                mTwoPane = false;
            }
        }

        MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movies);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putParcelable(MOVIE_KEY, movie);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra(MOVIE_KEY, movie);
            startActivity(intent);
        }
    }
}
