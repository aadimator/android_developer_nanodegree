package com.aadimator.android.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aadimator.android.bakingapp.R;
import com.aadimator.android.bakingapp.data.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aadam on 7/16/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context mContext;
    private List<Recipe> mRecipeList;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        mContext = context;
        mRecipeList = recipes;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipeList.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.recipeIngredients.setText(String.valueOf(recipe.getIngredients().size()));
        holder.recipeServings.setText(String.valueOf(recipe.getServings()));
        holder.recipeSteps.setText(String.valueOf(recipe.getSteps().size()));
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    public void updateRecipes(List<Recipe> recipes) {
        mRecipeList = recipes;
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_name_text_view)
        TextView recipeName;
        @BindView(R.id.recipe_ingredients_text_view)
        TextView recipeIngredients;
        @BindView(R.id.recipe_servings_text_view)
        TextView recipeServings;
        @BindView(R.id.recipe_steps_text_view)
        TextView recipeSteps;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
