package com.aadimator.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.adapters.RecipeAdapter;
import com.aadimator.bakingapp.models.Recipe;
import com.aadimator.bakingapp.networking.ApiUtils;
import com.aadimator.bakingapp.networking.RecipeService;
import com.aadimator.bakingapp.utils.OnItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Aadam on 20/06/2017.
 */

public class RecipeListFragment extends Fragment {

    private static final String LOG_TAG = RecipeListFragment.class.getSimpleName();
    private final String INSTANCE_KEY_RECIPE_LIST = "instance_key_recipe_list";

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.rv_recipe_list)
    RecyclerView mRecyclerView;
    private RecipeService mRecipeService;
    private RecipeAdapter mRecipeAdapter;
    private GridLayoutManager mLayoutManager;
    public ArrayList<Recipe> mRecipes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INSTANCE_KEY_RECIPE_LIST)) {
                mRecipes = savedInstanceState.getParcelableArrayList(INSTANCE_KEY_RECIPE_LIST);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ButterKnife.bind(this, rootView);

        mRecipeService = ApiUtils.getRecipeService();
        mRecipeAdapter = new RecipeAdapter(mRecipes, new OnItemClickListener<Recipe>() {
            @Override
            public void onClick(Recipe recipe, View view) {
                mListener.recipeSelected(recipe);
            }
        });
        mLayoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.recipe_list_columns));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        if (mRecipes == null || mRecipes.size() == 0) {
            loadRecipes();
        } else {
            mRecipeAdapter.updateRecipes(mRecipes);
        }

        return rootView;
    }


    public void loadRecipes() {
        mRecipeService.getRecipes().enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if (response.isSuccessful()) {
                    mRecipes = response.body();
                    mRecipeAdapter.updateRecipes(mRecipes);
                } else {
                    int statusCode = response.code();
                    Log.d(LOG_TAG, "Error. Status Code : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d(LOG_TAG, "Error loading from API");
                Log.d(LOG_TAG, t.getMessage());
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(INSTANCE_KEY_RECIPE_LIST, mRecipes);
        super.onSaveInstanceState(outState);
    }

    public interface OnFragmentInteractionListener {
        void recipeSelected(Recipe recipe);
    }

}
