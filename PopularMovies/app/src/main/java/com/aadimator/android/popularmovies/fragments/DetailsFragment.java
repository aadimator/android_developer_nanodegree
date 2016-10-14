package com.aadimator.android.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aadimator.android.popularmovies.R;
import com.aadimator.android.popularmovies.activities.MainActivity;
import com.aadimator.android.popularmovies.adapters.ReviewsAdapter;
import com.aadimator.android.popularmovies.adapters.TrailersAdapter;
import com.aadimator.android.popularmovies.data.MovieContract.MovieEntry;
import com.aadimator.android.popularmovies.helpers.QueryUtils;
import com.aadimator.android.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Aadam on 10/11/2016.
 */

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Movie> {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final int DETAIL_LOADER_ID = 2;

    private Movie mMovie;
    private boolean mDataLoaded = false;
    private TextView mTitle;
    private TextView mReleaseYear;
    private TextView mRating;
    private TextView mPlot;
    private ImageView mPoster;
    private TextView mEmptyTrailer;
    private TextView mEmptyReview;
    private ImageButton mFavoriteButton;

    private RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;
    private RecyclerView.Adapter mTrailerAdapter;
    private RecyclerView.Adapter mReviewsAdapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        bindViews(rootView);

        Bundle args = getArguments();

        if (args != null) {
            mMovie = args.getParcelable(MainActivity.MOVIE_KEY);
            updateUI();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMovie != null) {
            getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this).forceLoad();
        }
    }

    private void bindViews(View view) {
        mTrailersRecyclerView = (RecyclerView) view.findViewById(R.id.trailersRecyclerView);
        mReviewsRecyclerView = (RecyclerView) view.findViewById(R.id.reviewsRecyclerView);
        mTrailersRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager trailersLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(getContext());
        mTrailersRecyclerView.setLayoutManager(trailersLayoutManager);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

        mTitle = (TextView) view.findViewById(R.id.movieTitle);
        mTitle.setText(R.string.empty_view_title_text);
        mReleaseYear = (TextView) view.findViewById(R.id.movieReleaseYear);
        mRating = (TextView) view.findViewById(R.id.movieRating);
        mPlot = (TextView) view.findViewById(R.id.moviePlot);
        mPoster = (ImageView) view.findViewById(R.id.moviePoster);
        mFavoriteButton = (ImageButton) view.findViewById(R.id.favoriteButton);
        mFavoriteButton.setVisibility(View.INVISIBLE);

        mEmptyTrailer = (TextView) view.findViewById(R.id.empty_trailer_view);
        mEmptyReview = (TextView) view.findViewById(R.id.empty_review_view);
    }

    private void updateUI() {
        mTitle.setText(mMovie.getTitle());
        mReleaseYear.setText(mMovie.getReleaseYear());
        mRating.setText(getString(R.string.movieRatings, mMovie.getRating()));
        mPlot.setText(mMovie.getPlot());
        Picasso.with(getContext()).load(mMovie.getImageUrl()).error(R.drawable.error).into(mPoster);
        mFavoriteButton.setVisibility(View.VISIBLE);

        mTrailerAdapter = new TrailersAdapter(mMovie.getYoutubeLinks());
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);
        mReviewsAdapter = new ReviewsAdapter(mMovie.getReviews());
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        if (mMovie.isFavorite()) {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message = "";

                        if (mMovie.isFavorite()) {
                            // movie already in database, delete it
                            int deletedRows = getContext().getContentResolver().delete(MovieEntry.CONTENT_URI,
                                    MovieEntry.COLUMN_MOVIE_ID + "=?",
                                    new String[]{String.valueOf(mMovie.getId())});

                            if (deletedRows == 1) {
                                message = mMovie.getTitle() + " is removed from your favorites";
                                mMovie.setFavorite(false);
                            } else {
                                message = "Problem in removing from favorites";
                            }
                        } else {
                            // Save the current movie in the database
                            ContentValues values = new ContentValues();
                            values.put(MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
                            values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
                            try {
                                values.put(MovieEntry.COLUMN_MOVIE_IMAGE_PATH,
                                        saveToInternalStorage(
                                                Picasso.with(getContext())
                                                        .load(mMovie.getImageUrl()).get())
                                );
                            } catch (IOException e) {
                                values.put(MovieEntry.COLUMN_MOVIE_IMAGE_PATH, "No Image");
                                Log.d(LOG_TAG, "Can't download image", e);
                            }
                            values.put(MovieEntry.COLUMN_MOVIE_PLOT, mMovie.getPlot());
                            values.put(MovieEntry.COLUMN_MOVIE_RATING, mMovie.getRating());
                            values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseYear());

                            Uri newUri = getContext().getContentResolver().insert(MovieEntry.CONTENT_URI, values);
                            if (newUri == null) {
                                message = getString(R.string.fav_add_unsuccessful);
                            } else {
                                message = mMovie.getTitle() + " is added to your favorites";
                                mMovie.setFavorite(true);
                            }
                        }

                        final String finalMessage = message;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Show a toast message
                                Toast.makeText(getContext(), finalMessage, Toast.LENGTH_SHORT).show();
                                updateUI();
                            }
                        });
                    }
                }).start();
            }
        });

        if (mMovie.getTrailers() == null || mMovie.getTrailers().isEmpty()) { // show the empty view
            if (mDataLoaded) { // if we fetched data already, and no trailer
                mEmptyTrailer.setText(getString(R.string.no_trailer_available));
            } else { // if we still need to fetch the data
                mEmptyTrailer.setText(getString(R.string.fetching_trailers));
            }
            mEmptyTrailer.setVisibility(View.VISIBLE);
        } else { // when the data is loaded
            mEmptyTrailer.setVisibility(View.INVISIBLE);
        }

        if (mMovie.getReviews() == null || mMovie.getReviews().isEmpty()) { // show the empty view
            if (mDataLoaded) { // if we fetched data already, and no trailer
                mEmptyReview.setText(getString(R.string.no__review_available));
            } else { // if we still need to fetch the data
                mEmptyReview.setText(getString(R.string.fetching_reviews));
            }
            mEmptyReview.setVisibility(View.VISIBLE);
        } else { // when the data is loaded
            mEmptyReview.setVisibility(View.INVISIBLE);
        }
    }

    private String saveToInternalStorage(Bitmap bitmap) {
        ContextWrapper contextWrapper = new ContextWrapper((getContext()));
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, mMovie.getId() + ".png");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.i(LOG_TAG, "Image URI : " + Uri.fromFile(myPath).toString());
        return Uri.fromFile(myPath).toString();
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        return new DetailsLoader(getContext(), mMovie);
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        mDataLoaded = true;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {
    }
}

class DetailsLoader extends AsyncTaskLoader<Movie> {

    private Movie mMovie;

    public DetailsLoader(Context context, Movie movie) {
        super(context);
        mMovie = movie;
    }

    @Override
    public Movie loadInBackground() {
        if (mMovie == null) {
            return null;
        }
        // get list of trailers for the current movie
        mMovie.setTrailers(QueryUtils.fetchTrailersData(QueryUtils.createTrailersUrlPath(mMovie.getId())));
        // get list of reviews for the current movie
        mMovie.setReviews(QueryUtils.fetchReviewsData(QueryUtils.createReviewsUrlPath(mMovie.getId())));

        return mMovie;
    }
}

