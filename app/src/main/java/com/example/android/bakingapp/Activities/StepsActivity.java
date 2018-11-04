package com.example.android.bakingapp.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Model.Step;
import com.example.android.bakingapp.Fragments.StepsFragment;

public class StepsActivity extends AppCompatActivity {

    private Step step;
    private String recipeName;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("RecipeNameState", recipeName);
        outState.putParcelable("StepObjectState", step);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        if (savedInstanceState != null){
            step = savedInstanceState.getParcelable("StepObjectState");
            recipeName = savedInstanceState.getString("RecipeNameState");

            //Toast.makeText(this, "FragmentActivity part 2", Toast.LENGTH_SHORT).show();
            Bundle b = new Bundle();
            b.putParcelable("StepObject", step );
            b.putString("RecipeName", recipeName);
            //this int will be used in StepFragment to identify savedInstanceState()
            b.putInt("ID", 500);
            StepsFragment stepsFragment = new StepsFragment();
            stepsFragment.setArguments(b);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.steps_frag_container, stepsFragment).commit();
        }

        if (savedInstanceState == null){
            Intent intent = getIntent();
            if (intent.hasExtra("StepObject")){
                step = intent.getParcelableExtra("StepObject");
                recipeName = intent.getStringExtra("Name");
            }

            Bundle b = new Bundle();
            b.putParcelable("StepObject", step );
            b.putString("Name", recipeName);
            //this int will be used in StepFragment to identify savedInstanceState()
            b.putInt("ID", -100);
            StepsFragment stepsFragment = new StepsFragment();
            stepsFragment.setArguments(b);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.steps_frag_container, stepsFragment).commit();
        }
    }
}
