package com.example.android.bakingapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.Model.ImageAndRecipe;
import com.example.android.bakingapp.Model.Ingredient;
import com.example.android.bakingapp.Model.Recipe;
import com.example.android.bakingapp.Model.Step;
import com.example.android.bakingapp.R;

import java.util.ArrayList;

/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class IngredientsFragment extends Fragment {

    ImageAndRecipe imageAndRecipe;
    ArrayList<Ingredient> ingredients;
    ArrayList<Step> steps;
    private boolean tSize = true;
    private static ArrayList<Recipe> recipeList;
    private static ArrayList<ImageAndRecipe> recipeListsss;


    public IngredientsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.ingredients_list, container, false);

        TextView textView = rootView.findViewById(R.id.ingredients_title);
        TextView textView1 = rootView.findViewById(R.id.ingredients_lists);


        if (getArguments().getParcelableArrayList("AllRecipes") != null){
            //Toast.makeText(getContext(), "I got the argument", Toast.LENGTH_SHORT).show();
            recipeList = getArguments().getParcelableArrayList("AllRecipes");
            if (recipeList != null){
                //textView.append("\n");
                for (int i = 0; i < recipeList.size(); i++){
                    ingredients = recipeList.get(i).getIngredients();
                    if (ingredients != null){
                        textView1.append(recipeList.get(i).getName() + "\n\n");
                        for (int j = 0; j < ingredients.size(); j++){
                            textView1.append(ingredients.get(j).getIngredient() + ": "
                                    + ingredients.get(j).getQuantity() + " "
                                    + ingredients.get(j).getMeasure()  + "\n");
                        }
                        textView1.append("\n");
                    }
                }
            }
        }

        if (getArguments().getParcelableArrayList("AllRecipesss") != null){

            recipeListsss = getArguments().getParcelableArrayList("AllRecipesss");
            if (recipeListsss != null){
                //Toast.makeText(getContext(), "I got the offline argument", Toast.LENGTH_SHORT).show();
                //textView.append("\n");
                for (int i = 0; i < recipeListsss.size(); i++){
                    ingredients = recipeListsss.get(i).getmIngredients();
                    if (ingredients != null){
                        textView1.append(recipeListsss.get(i).getTxt() + "\n\n");
                        for (int j = 0; j < ingredients.size(); j++){
                            textView1.append(ingredients.get(j).getIngredient() + ": "
                                    + ingredients.get(j).getQuantity() + " "
                                    + ingredients.get(j).getMeasure()  + "\n");
                        }
                        textView1.append("\n");
                    }
                }
            }
        }
        return rootView;
    }
}
