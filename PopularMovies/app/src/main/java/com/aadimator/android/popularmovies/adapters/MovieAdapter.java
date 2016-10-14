package com.aadimator.android.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aadimator.android.popularmovies.R;
import com.aadimator.android.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Aadam on 10/9/2016.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder viewHolder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_grid_view, parent, false
            );

            viewHolder = new ViewHolder();
            viewHolder.poster = (ImageView) listItemView.findViewById(R.id.poster);
            listItemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) listItemView.getTag();
        }

        final Movie currentMovie = getItem(position);

        Picasso.with(getContext()).load(currentMovie.getImageUrl()).error(R.drawable.error).into(viewHolder.poster);

        return listItemView;
    }

    static class ViewHolder {
        ImageView poster;
    }

}
