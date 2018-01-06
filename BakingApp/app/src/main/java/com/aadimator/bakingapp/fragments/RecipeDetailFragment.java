package com.aadimator.bakingapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.adapters.IngredientAdapter;
import com.aadimator.bakingapp.adapters.StepAdapter;
import com.aadimator.bakingapp.models.Recipe;
import com.aadimator.bakingapp.models.Step;
import com.aadimator.bakingapp.utils.OnItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aadam on 21/06/2017.
 */

public class RecipeDetailFragment extends Fragment {


    private static final String BUNDLE_RECIPE_DATA = "com.aadimator.bakingapp.fragments.recipe_data";
    private static final String INSTANCE_KEY_RECIPE = "instance_key_recipe";

    private OnFragmentInteractionListener mListener;

    private Recipe mRecipe;

    @BindView(R.id.rv_recipe_detail_steps)
    RecyclerView mRecyclerViewSteps;
    @BindView(R.id.rv_recipe_detail_ingredients)
    RecyclerView mRecyclerViewIngredients;

    public static RecipeDetailFragment newInstance(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_RECIPE_DATA, recipe);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if ((arguments != null) && (arguments.containsKey(BUNDLE_RECIPE_DATA))) {
            mRecipe = arguments.getParcelable(BUNDLE_RECIPE_DATA);
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INSTANCE_KEY_RECIPE)) {
                mRecipe = savedInstanceState.getParcelable(INSTANCE_KEY_RECIPE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mRecipe.getName());

        mRecyclerViewSteps.setHasFixedSize(true);
        mRecyclerViewIngredients.setHasFixedSize(true);
        mRecyclerViewSteps.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));

        StepAdapter stepAdapter = new StepAdapter(mRecipe.getSteps(), new OnItemClickListener<Step>() {
            @Override
            public void onClick(Step step, View view) {
                mListener.stepSelected(mRecipe.getSteps(), step.getId(), mRecipe.getName(), view);
            }
        });

        mRecyclerViewSteps.setAdapter(stepAdapter);
        mRecyclerViewIngredients.setAdapter(new IngredientAdapter(mRecipe.getIngredients()));

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void stepSelected(ArrayList<Step> stepList, int currentStep, String recipeName, View view);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(INSTANCE_KEY_RECIPE, mRecipe);
    }
}
