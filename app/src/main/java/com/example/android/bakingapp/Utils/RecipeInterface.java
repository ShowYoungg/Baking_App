
package com.example.android.bakingapp.Utils;

import com.example.android.bakingapp.Model.Recipe;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface RecipeInterface {
    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipe();

    @Streaming
    @GET
    Call<ResponseBody> downloadMedia(@Url String url);
}