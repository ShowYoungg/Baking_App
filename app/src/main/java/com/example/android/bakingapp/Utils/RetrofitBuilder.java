
package com.example.android.bakingapp.Utils;

import com.example.android.bakingapp.Utils.RecipeInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public final class RetrofitBuilder {
    static RecipeInterface recipeInterface;

    public static RecipeInterface Retrieve() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        recipeInterface = new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build().create(RecipeInterface.class);

        return recipeInterface;
    }
}

