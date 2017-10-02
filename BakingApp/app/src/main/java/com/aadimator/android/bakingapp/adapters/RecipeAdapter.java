package com.aadimator.android.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aadimator.android.bakingapp.R;
import com.aadimator.android.bakingapp.activities.RecipeStepListActivity;
import com.aadimator.android.bakingapp.datamodel.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aadam on 7/16/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> mRecipeList;

    public RecipeAdapter(List<Recipe> recipes) {
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
        return mRecipeList.size();
    }

    public void updateRecipes(List<Recipe> recipes) {
        mRecipeList = recipes;
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView((R.id.recipe_image))
        ImageView recipeImage;
        @BindView(R.id.recipe_name)
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    Recipe recipe = mRecipeList.get(position);
//                    Toast.makeText(view.getContext(), "ID : " + recipe.getId(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), RecipeStepListActivity.class);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
