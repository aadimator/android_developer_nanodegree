package com.aadimator.android.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aadimator.android.bakingapp.adapters.RecipeAdapter;
import com.aadimator.android.bakingapp.data.model.Recipe;
import com.aadimator.android.bakingapp.data.remote.RecipeService;
import com.aadimator.android.bakingapp.helpers.ApiUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecipeService mRecipeService;
    private RecyclerView mRecyclerView;
    private RecipeAdapter mRecipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipeService = ApiUtils.getRecipeService();
        mRecipeAdapter = new RecipeAdapter(this, new ArrayList<Recipe>(0));
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadRecipes();
    }

    public void loadRecipes() {
        mRecipeService.getRecipes().enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if (response.isSuccessful()) {
                    mRecipeAdapter.updateRecipes(response.body());
                    Log.d(LOG_TAG, response.body().get(0).getName());
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

}
