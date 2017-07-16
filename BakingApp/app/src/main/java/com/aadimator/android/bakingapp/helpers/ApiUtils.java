package com.aadimator.android.bakingapp.helpers;

import com.aadimator.android.bakingapp.data.remote.RecipeService;
import com.aadimator.android.bakingapp.data.remote.RetrofitClient;

/**
 * Created by Aadam on 7/16/2017.
 */

public class ApiUtils {

    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";

    public static RecipeService getRecipeService() {
        return RetrofitClient.getClient(BASE_URL).create(RecipeService.class);
    }
}
