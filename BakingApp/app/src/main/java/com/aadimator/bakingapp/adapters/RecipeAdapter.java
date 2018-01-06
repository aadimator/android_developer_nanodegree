package com.aadimator.bakingapp.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.models.Recipe;
import com.aadimator.bakingapp.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aadam on 20/06/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> mRecipeList;
    private OnItemClickListener mRecipeClickListener;

    public RecipeAdapter(List<Recipe> recipes, OnItemClickListener<Recipe> recipeOnItemClickListener) {
        mRecipeList = recipes;
        mRecipeClickListener = recipeOnItemClickListener;
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
        holder.mRecipe = recipe;

        // Only load image if there's a url for the image
        // else, load default
        if (!recipe.getImage().isEmpty()) {
            Picasso.with(holder.itemView.getContext())
                    .load(recipe.getImage())
                    .placeholder(R.drawable.cupcake)
                    .error(R.drawable.cupcake)
                    .into(holder.recipeImage);
        } else {
            Picasso.with(holder.itemView.getContext())
                    .load(R.drawable.cupcake)
                    .into(holder.recipeImage);
        }

        holder.recipeName.setText(recipe.getName());
        holder.recipeIngredients.setText(String.valueOf(recipe.getIngredients().size()));
        holder.recipeServings.setText(String.valueOf(recipe.getServings()));
        holder.recipeSteps.setText(String.valueOf(recipe.getSteps().size()));
    }

    @Override
    public int getItemCount() {
        return (mRecipeList == null) ? 0 : mRecipeList.size();
    }

    public void updateRecipes(List<Recipe> recipes) {
        mRecipeList = recipes;
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Recipe mRecipe;

        @BindView((R.id.iv_recipe_image))
        ImageView recipeImage;
        @BindView(R.id.tv_recipe_name)
        TextView recipeName;
        @BindView(R.id.tv_recipe_ingredients)
        TextView recipeIngredients;
        @BindView(R.id.tv_recipe_servings)
        TextView recipeServings;
        @BindView(R.id.tv_recipe_steps)
        TextView recipeSteps;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mRecipeClickListener.onClick(mRecipe, view);
        }
    }
}
