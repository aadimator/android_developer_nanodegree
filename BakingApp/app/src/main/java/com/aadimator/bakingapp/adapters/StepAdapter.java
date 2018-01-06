package com.aadimator.bakingapp.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.models.Step;
import com.aadimator.bakingapp.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aadam on 21/06/2017.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.VHolder> {

    final private OnItemClickListener mStepClickListener;
    final private ArrayList<Step> mStepList;

    public StepAdapter(ArrayList<Step> steps, OnItemClickListener<Step> onItemClickListener) {
        mStepClickListener = onItemClickListener;
        this.mStepList = steps;
    }


    @Override
    public VHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.step_list_item, viewGroup, false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(VHolder holder, int position) {
        Step step = mStepList.get(position);
        holder.mStep = step;

        if (step.getVideoURL() != null && !step.getVideoURL().matches("")) {
            if (step.getThumbnailURL().isEmpty()) {
                holder.videoThumb.setImageResource(R.drawable.ic_videocam_black_24dp);
            } else {
                Picasso.with(holder.itemView.getContext())
                        .load(step.getThumbnailURL())
                        .placeholder(R.drawable.ic_videocam_black_24dp)
                        .error(R.drawable.ic_videocam_black_24dp)
                        .into(holder.videoThumb);
            }
        } else {
            holder.videoThumb.setImageResource(R.drawable.ic_videocam_off_black_24dp);
        }
        holder.stepNumber.setText(String.valueOf(step.getId()) + ": ");
        holder.shotDesc.setText(step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        return mStepList.size();
    }

    class VHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Step mStep;

        @BindView(R.id.iv_step_item_video_thumb)
        ImageView videoThumb;
        @BindView(R.id.tv_step_list_step_number)
        TextView stepNumber;
        @BindView(R.id.tv_step_list_step_short_desc)
        TextView shotDesc;
        @BindView(R.id.cv_step_item_holder)
        CardView item_holder;

        VHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            item_holder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mStepClickListener.onClick(mStep, v);
        }
    }
}
