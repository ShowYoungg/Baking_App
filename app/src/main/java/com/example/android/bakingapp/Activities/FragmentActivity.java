package com.example.android.bakingapp.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.bakingapp.Model.ImageAndRecipe;
import com.example.android.bakingapp.Model.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Fragments.RecipeDetailFragment;
import com.example.android.bakingapp.Model.Step;
import com.example.android.bakingapp.Fragments.StepsFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepListClickListener {

    private ImageAndRecipe imageAndRecipe;
    private ArrayList<Ingredient> ingredients;
    private Bundle bundle;
    private List<ImageAndRecipe> list;
    private String nameOfRecipe;
    private Step step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Toast.makeText(this, "FragmentActivity", Toast.LENGTH_SHORT).show();
        if (savedInstanceState == null){
            //Toast.makeText(this, "FragmentActivity", Toast.LENGTH_SHORT).show();\
            Intent intent = getIntent();
            if (intent == null){
                Toast.makeText(this, "Error getting intent", Toast.LENGTH_LONG).show();
                finish();
            }

            bundle = new Bundle();
            if (intent != null){
                imageAndRecipe = intent.getParcelableExtra("ImageAndRecipeObject");
            }
            Bundle b = new Bundle();
            b.putParcelable("ImageAndRecipe", imageAndRecipe );
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            recipeDetailFragment.setArguments(b);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.first_frag_container, recipeDetailFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageAndRecipeState", imageAndRecipe);
        outState.putString("recipeNameState", nameOfRecipe );
        outState.putParcelable("StepState", step);
    }

    //Navigation arrow on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStepListSelected(Step steps, int position, ImageAndRecipe imageAndRecipe) {

        //Toast.makeText(this, "We are here " + position, Toast.LENGTH_SHORT).show();
        nameOfRecipe = imageAndRecipe.getTxt();
        step = steps;

        Bundle b = new Bundle();
        b.putParcelable("Step", steps );
        b.putString("recipeName", nameOfRecipe);
        b.putInt("ID", -100);
        StepsFragment fragment = new StepsFragment();
        fragment.setArguments(b);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.steps_frag_container, fragment).commit();
    }
}
