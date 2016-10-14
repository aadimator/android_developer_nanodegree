package com.aadimator.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MovieAdapter.MOVIE_KEY)) {
            updateUI((Movie) intent.getParcelableExtra(MovieAdapter.MOVIE_KEY));
        }
    }

    private void updateUI(Movie movie) {

        TextView title = (TextView) findViewById(R.id.movieTitle);
        TextView releaseYear = (TextView) findViewById(R.id.movieReleaseYear);
        TextView rating = (TextView) findViewById(R.id.movieRating);
        TextView plot = (TextView) findViewById(R.id.moviePlot);
        ImageView poster = (ImageView) findViewById(R.id.moviePoster);

        title.setText(movie.getTitle());
        releaseYear.setText(movie.getReleaseDate());
        rating.setText(movie.getRating());
        plot.setText(movie.getPlot());
        Picasso.with(this).load(movie.getImageUrl()).into(poster);
    }
}
