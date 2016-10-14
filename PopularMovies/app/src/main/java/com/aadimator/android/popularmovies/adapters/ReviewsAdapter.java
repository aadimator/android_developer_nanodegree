package com.aadimator.android.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aadimator.android.popularmovies.R;
import com.aadimator.android.popularmovies.models.Review;

import java.util.List;

/**
 * Created by Aadam on 10/11/2016.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
        }
    }

    public ReviewsAdapter(List<Review> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // get element from your dataset at this position
        // replace the contents of the view with that element
        TextView authorView = (TextView) holder.mLinearLayout.findViewById(R.id.reviewAuthor);
        authorView.setText(mDataSet.get(position).getAuthor());

        TextView contentView = (TextView) holder.mLinearLayout.findViewById(R.id.reviewContent);
        contentView.setText(mDataSet.get(position).getContent());
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
