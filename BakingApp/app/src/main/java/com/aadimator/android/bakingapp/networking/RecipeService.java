package com.aadimator.android.bakingapp.networking;

import com.aadimator.android.bakingapp.datamodel.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Aadam on 7/16/2017.
 */

public interface RecipeService {
    @GET(" ")
    Call<ArrayList<Recipe>> getRecipes();
}
