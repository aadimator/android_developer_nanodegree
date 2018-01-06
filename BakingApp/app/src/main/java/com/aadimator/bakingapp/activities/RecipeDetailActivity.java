package com.aadimator.bakingapp.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.fragments.RecipeDetailFragment;
import com.aadimator.bakingapp.fragments.StepFragment;
import com.aadimator.bakingapp.models.Recipe;
import com.aadimator.bakingapp.models.Step;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnFragmentInteractionListener {

    private static final String BUNDLE_RECIPE_DATA = "com.aadimator.bakingapp.activities.recipe_data";

    public static Intent newIntent(Context packageContext, Recipe recipe) {
        Intent intent = new Intent(packageContext, RecipeDetailActivity.class);
        intent.putExtra(BUNDLE_RECIPE_DATA, recipe);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        FragmentManager fm = getSupportFragmentManager();
        Fragment detailFragment = fm.findFragmentById(R.id.fragment_container_step);
        Recipe recipe = getIntent().getExtras().getParcelable(BUNDLE_RECIPE_DATA);

        if (detailFragment == null) {
            detailFragment = RecipeDetailFragment.newInstance(recipe);
            fm.beginTransaction()
                    .add(R.id.fragment_container_step, detailFragment)
                    .commit();

            if (getResources().getBoolean(R.bool.isTablet)) {
                Fragment stepFragment = fm.findFragmentById(R.id.fragment_container_step_detail);
                if (stepFragment == null) {
                    stepFragment = StepFragment.newInstance(recipe.getSteps().get(0));
                    fm.beginTransaction()
                            .replace(R.id.fragment_container_step_detail, stepFragment)
                            .commit();
                }
            }
        }
    }

    @Override
    public void stepSelected(ArrayList<Step> stepList, int currentStep, String recipeName, View view) {
        if (!getResources().getBoolean(R.bool.isTablet)) {
            Intent intent = StepActivity.newIntent(this, stepList, currentStep, recipeName);
            startActivity(intent);
        } else {
            Fragment stepFragment = StepFragment.newInstance(stepList.get(currentStep));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_step_detail, stepFragment)
                    .commit();
        }
    }
}
