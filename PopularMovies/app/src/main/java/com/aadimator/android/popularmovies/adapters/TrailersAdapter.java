package com.aadimator.android.popularmovies.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aadimator.android.popularmovies.R;

import java.util.List;

/**
 * Created by Aadam on 10/11/2016.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private List<String> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
        }
    }

    public TrailersAdapter(List<String> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // get element from your dataset at this position
        // replace the contents of the view with that element
        TextView textView = (TextView) holder.mLinearLayout.findViewById(R.id.trailerItemTextView);
        textView.setText("Trailer " + (position + 1)); // Trailer 1, Trailer 2 etc

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mDataSet.get(position)));
                if (intent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
