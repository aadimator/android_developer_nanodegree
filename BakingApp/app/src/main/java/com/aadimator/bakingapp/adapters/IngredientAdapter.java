package com.aadimator.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.models.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aadam on 21/06/2017.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.VHolder> {

    final private ArrayList<Ingredient> mIngredients;

    public IngredientAdapter(ArrayList<Ingredient> ingredients) {
        this.mIngredients = ingredients;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ingredient_item, viewGroup, false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(VHolder holder, int position) {
        holder.ingredient.setText(mIngredients.get(position).getIngredient());
        holder.measure.setText(mIngredients.get(position).getMeasure());
        holder.quantity.setText(mIngredients.get(position).getQuantity() + "");
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    class VHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ingredient)
        TextView ingredient;
        @BindView(R.id.tv_quantity)
        TextView quantity;
        @BindView(R.id.tv_measure)
        TextView measure;


        VHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
