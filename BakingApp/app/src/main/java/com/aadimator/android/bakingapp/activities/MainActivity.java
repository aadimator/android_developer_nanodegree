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
    private RecipeService mRecipeService;
    private RecipeAdapter mRecipeAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRecipeService = ApiUtils.getRecipeService();
        mRecipeAdapter = new RecipeAdapter(this, new ArrayList<Recipe>(0));

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
