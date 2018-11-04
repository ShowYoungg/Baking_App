package com.example.android.bakingapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.bakingapp.Adapters.RecipeDetailAdapter;
import com.example.android.bakingapp.Fragments.StepsFragment;
import com.example.android.bakingapp.Model.ImageAndRecipe;
import com.example.android.bakingapp.Model.Recipe;
import com.example.android.bakingapp.Model.Step;
import com.example.android.bakingapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.Activities.MainActivity.JSON_KEY;
import static com.example.android.bakingapp.Services.StackRemoteViewsFactory.getUpdate;


/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class RecipeDetailFragment extends Fragment {

    private RecyclerView recyclerView;
    private OnStepListClickListener mCallback;
    private boolean mTwoPane;
    private ArrayList<ImageAndRecipe> imageAndRecipeArrayList;
    private ArrayList<Recipe> recipeList;
    private ArrayList<Step> steps;
    private Step step;
    private RecipeDetailAdapter recipeDetailAdapter;
    private String jsonResultConvertedToString;
    public static SharedPreferences sharedPreferences;
    public static int recipeNameId;
    public static SharedPreferences recipeNameSharedPreferences;
    public static final String RECIPE_NAME = "RECIPE_NAME_FOR_WIDGET";
    public static ImageAndRecipe imageAndRecipe;
    private String recipeName;





    public interface OnStepListClickListener{
        void onStepListSelected(Step steps, int position, ImageAndRecipe imageAndRecipe);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (OnStepListClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement onStepListClickListener");
        }
    }

    public RecipeDetailFragment(){
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageAndRecipeState", imageAndRecipe);
        outState.putInt("RecipeID", recipeNameId);
        outState.putParcelableArrayList("StepState", steps);
        outState.putString("RECIPE_NAME", recipeName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_recycler_view, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        recipeNameSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        recyclerView = rootView.findViewById(R.id.recycler_view);
        imageAndRecipeArrayList = new ArrayList<>();
        recipeList = new ArrayList<>();
        steps = new ArrayList<>();

        if (rootView.findViewById(R.id.steps_frag) != null){
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        if (recipeNameSharedPreferences != null){
            recipeNameId = recipeNameSharedPreferences.getInt( RECIPE_NAME, recipeNameId);
            //Toast.makeText(getContext(), "id retrieved " + recipeNameId, Toast.LENGTH_SHORT).show();
        }

        if (getArguments() != null){
            imageAndRecipe = getArguments().getParcelable("ImageAndRecipe");
            if (imageAndRecipe != null){
                steps = imageAndRecipe.getmSteps();
                recipeName = imageAndRecipe.getTxt();
            }
        }


        if (savedInstanceState != null){
            imageAndRecipe = savedInstanceState.getParcelable("ImageAndRecipeState");
            if (recipeNameSharedPreferences != null){
                recipeNameId = recipeNameSharedPreferences.getInt( RECIPE_NAME, recipeNameId);
            } else{
                recipeNameId = -100;
            }
            getUpdate(recipeNameId);
            if (sharedPreferences != null){
                recipeName = sharedPreferences.getString("NAMEOFRECIPE", "");
            } else {
                recipeName = savedInstanceState.getString("RECIPE_NAME", recipeName);
            }
        }

        jsonResultConvertedToString = sharedPreferences.getString(JSON_KEY, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Recipe>>(){}.getType();
        recipeList = gson.fromJson(jsonResultConvertedToString, type);
        if (recipeList != null) {
            imageAndRecipeArrayList.add(new ImageAndRecipe( R.drawable.nutella_pie,
                    recipeList.get(0).getName(), recipeList.get(0).getIngredients(),
                    recipeList.get(0).getSteps() ));
            imageAndRecipeArrayList.add(new ImageAndRecipe( R.drawable.brownies,
                    recipeList.get(1).getName(), recipeList.get(1).getIngredients(),
                    recipeList.get(1).getSteps() ));
            imageAndRecipeArrayList.add(new ImageAndRecipe( R.drawable.yellow_cake,
                    recipeList.get(2).getName(), recipeList.get(2).getIngredients(),
                    recipeList.get(2).getSteps() ));
            imageAndRecipeArrayList.add(new ImageAndRecipe( R.drawable.cheese_cake,
                    recipeList.get(3).getName(), recipeList.get(3).getIngredients(),
                    recipeList.get(3).getSteps() ));
        }

        recipeDetailAdapter = new RecipeDetailAdapter( getContext(),
                imageAndRecipeArrayList, mCallback, mTwoPane);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recipeDetailAdapter);

        //This recipeName will be used when navigating back from StepFragment on phone not on tablet
        if (recipeName == null && sharedPreferences != null){
            recipeName = sharedPreferences.getString("NAMEOFRECIPE", "");
            getScrollPosition();
        } else {
            getScrollPosition();
        }

        //Toast.makeText(getContext(), "S "+ recipeName, Toast.LENGTH_SHORT).show();


        if (mTwoPane){
            StepsFragment fragment = new StepsFragment();
            FragmentManager fragmentManager = getFragmentManager();

            if (savedInstanceState == null){
                fragmentManager.beginTransaction().add(R.id.steps_frag_container, fragment).commit();
            } else {
                Bundle b = new Bundle();
                steps = savedInstanceState.getParcelableArrayList("StepState");
                recipeName = savedInstanceState.getString("RECIPE_NAME");
                if (steps != null){
                    switch (recipeName){
                        case "Nutella Pie":

                            step = steps.get(0);
                            break;

                        case "Brownies":
                            step = steps.get(1);
                            break;

                        case "Yellow Cake":
                            step = steps.get(2);
                            break;

                        case "Cheesecake":
                            step = steps.get(3);
                            break;
                        default:
                            step = steps.get(0);
                            break;
                    }
                }
                b.putParcelable("RECIPE_STEP", step);
                b.putString("NAME_OF_RECIPE", recipeName);
                fragment.setArguments(b);
                fragmentManager.beginTransaction().replace(R.id.steps_frag_container, fragment).commit();
            }
        } else {

        }
        return rootView;
    }

    private void getScrollPosition() {
        if (recipeName != null){
            switch (recipeName){
                case "Nutella Pie":
                    recyclerView.scrollToPosition(0);
                    break;

                case "Brownies":
                    recyclerView.scrollToPosition(1);
                    break;

                case "Yellow Cake":
                    recyclerView.scrollToPosition(2);
                    break;

                case "Cheesecake":
                    recyclerView.scrollToPosition(3);
                    break;
                default:
                    recyclerView.scrollToPosition(0);
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static void saveAdapterChosenName ( String recipeName){
        SharedPreferences.Editor recipeNameFromAdapter = sharedPreferences.edit();
        recipeNameFromAdapter.putString("NAMEOFRECIPE", recipeName);
        recipeNameFromAdapter.commit();
    }

    /**
     * This method assigns different id to each recipe name and uses this id to retrieve the order
     * in which the StackView in IngredientsService2 displays recipe list based on user preference.
     */
    public static void assignId( ImageAndRecipe imageAndRecipe){
        if (imageAndRecipe != null){
            switch (imageAndRecipe.getTxt()){
                case "Nutella Pie":
                    recipeNameId = 0;
                    break;

                case "Brownies":
                    recipeNameId = 1;
                    break;

                case "Yellow Cake":
                    recipeNameId = 2;
                    break;

                case "Cheesecake":
                    recipeNameId = 3;
                    break;

                default:
                    if (recipeNameSharedPreferences != null){
                        recipeNameId = recipeNameSharedPreferences.getInt( RECIPE_NAME, recipeNameId);
                    } else {
                        recipeNameId = -100;
                    }
                    break;
            }
            getUpdate(recipeNameId);
            SharedPreferences.Editor recipeNameIdForWidget = recipeNameSharedPreferences.edit();
            recipeNameIdForWidget.putInt(RECIPE_NAME, recipeNameId);
            recipeNameIdForWidget.commit();
        }
    }
}
