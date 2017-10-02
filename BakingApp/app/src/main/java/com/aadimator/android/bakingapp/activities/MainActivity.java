package com.aadimator.android.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aadimator.android.bakingapp.R;
import com.aadimator.android.bakingapp.adapters.RecipeAdapter;
import com.aadimator.android.bakingapp.datamodel.Recipe;
import com.aadimator.android.bakingapp.networking.ApiUtils;
import com.aadimator.android.bakingapp.networking.RecipeService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String RECIPE_LIST_KEY = "recipe_list_key";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private RecipeService mRecipeService;
    private RecipeAdapter mRecipeAdapter;
    private ArrayList<Recipe> mRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPE_LIST_KEY)) {
                mRecipes = savedInstanceState.getParcelableArrayList(RECIPE_LIST_KEY);
            }
        }

        mRecipeService = ApiUtils.getRecipeService();
        mRecipeAdapter = new RecipeAdapter(new ArrayList<Recipe>(0));

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getInteger(R.integer.recipe_list_columns)));
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mRecipes == null || mRecipes.size() == 0) {
            loadRecipes();
        } else {
            mRecipeAdapter.updateRecipes(mRecipes);
        }

    }

    public void loadRecipes() {
        mRecipeService.getRecipes().enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if (response.isSuccessful()) {
                    mRecipes = response.body();
                    mRecipeAdapter.updateRecipes(mRecipes);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RECIPE_LIST_KEY, mRecipes);
        super.onSaveInstanceState(outState);
    }

}
