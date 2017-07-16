package com.aadimator.android.bakingapp.data.remote;

import com.aadimator.android.bakingapp.data.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Aadam on 7/16/2017.
 */

public interface RecipeService {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
