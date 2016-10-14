package com.aadimator.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Aadam on 10/9/2016.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    public static final String MOVIE_KEY = "currentMovie";

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_grid_view, parent, false
            );
        }

        final Movie currentMovie = getItem(position);

        ImageView posterImageView = (ImageView) listItemView.findViewById(R.id.poster);
        Picasso.with(getContext()).load(currentMovie.getImageUrl()).into(posterImageView);

        posterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailsActivity.class)
                        .putExtra(MOVIE_KEY, currentMovie);
                getContext().startActivity(intent);
            }
        });

        return listItemView;
    }
}
