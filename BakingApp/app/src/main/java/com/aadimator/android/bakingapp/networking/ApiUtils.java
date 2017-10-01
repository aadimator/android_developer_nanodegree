package com.aadimator.android.bakingapp.networking;

/**
 * Created by Aadam on 7/16/2017.
 */

public class ApiUtils {

    public static final String BASE_URL = "http://go.udacity.com/android-baking-app-json/";

    public static RecipeService getRecipeService() {
        return RetrofitClient.getClient(BASE_URL).create(RecipeService.class);
    }
}
