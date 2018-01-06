package com.aadimator.bakingapp.networking;

/**
 * Created by Aadam on 7/16/2017.
 */

import com.aadimator.bakingapp.models.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {
    @GET(" ")
    Call<ArrayList<Recipe>> getRecipes();
}
