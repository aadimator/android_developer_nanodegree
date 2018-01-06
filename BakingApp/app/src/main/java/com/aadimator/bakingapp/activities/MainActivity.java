package com.aadimator.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.fragments.RecipeListFragment;
import com.aadimator.bakingapp.models.Recipe;
import com.aadimator.bakingapp.widgets.RecipeWidgetService;

public class MainActivity extends AppCompatActivity implements RecipeListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new RecipeListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, RecipeListFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void recipeSelected(Recipe recipe) {
        RecipeWidgetService.startActionUpdateRecipeWidgets(this, recipe);
        Intent intent = RecipeDetailActivity.newIntent(this, recipe);
        startActivity(intent);
    }
}
