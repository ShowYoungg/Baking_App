package com.example.android.bakingapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.Model.ImageAndRecipe;
import com.example.android.bakingapp.Model.Ingredient;
import com.example.android.bakingapp.Model.Step;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Fragments.RecipeDetailFragment;

import java.util.ArrayList;

import static com.example.android.bakingapp.Fragments.RecipeDetailFragment.assignId;
import static com.example.android.bakingapp.Fragments.RecipeDetailFragment.saveAdapterChosenName;

/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.RecipeDetailViewHolder>{

    private ArrayList<Ingredient> ingredients;
    private RecyclerView recyclerView;
    private ArrayList<Step> steps;
    private String recipeName;
    private StepsAdapter stepsAdapter;
    private int mRecipeNameId;
    private ArrayList<ImageAndRecipe> mImageAndRecipeList;
    private Context mContext;
    private boolean mTwoPane;
    private final String RECIPE_NAME= "RECIPE_NAME";
    private ImageAndRecipe imageAndRecipe;
    private SharedPreferences recipeNameSharedPreferences;
    private SparseBooleanArray mCheckedItem = new SparseBooleanArray();




    RecipeDetailFragment.OnStepListClickListener mCallback;


    public RecipeDetailAdapter( Context context, ArrayList<ImageAndRecipe> imageAndRecipeList,
                                RecipeDetailFragment.OnStepListClickListener listener,
                                boolean twoPane){


        this.mContext = context;
        this.mImageAndRecipeList = imageAndRecipeList;
        this.mCallback = listener;
        this.mTwoPane = twoPane;
        //this.mRecipeNameId = recipeNameId;
        notifyDataSetChanged();
    }


    @Override
    public RecipeDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.fragment_recipe;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        RecipeDetailAdapter.RecipeDetailViewHolder viewHolder = new RecipeDetailAdapter.RecipeDetailViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder( final RecipeDetailViewHolder holder, int position) {

        imageAndRecipe = mImageAndRecipeList.get(position);
        populateUI( holder.textView1, imageAndRecipe);
        holder.checkBox.setTag(position);

        recyclerView.setTag(position);
        holder.checkBox.setChecked(mCheckedItem.get(position));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.checkBox.isChecked()){
                    //do nothing except default behaviour; or, if something is stored in the
                    // SharedPreferences, edit and assign default value
                    if (recipeNameSharedPreferences != null){
                        mRecipeNameId = -100;
                        SharedPreferences.Editor recipeNameIdForWidget = recipeNameSharedPreferences.edit();
                        recipeNameIdForWidget.putInt( RECIPE_NAME, mRecipeNameId);
                        recipeNameIdForWidget.commit();
                    }
                } else {

                    int position = holder.getAdapterPosition();
                    final boolean newValue = holder.checkBox.isChecked();

                    mCheckedItem.put(position, newValue);
                    imageAndRecipe = mImageAndRecipeList.get(position);
                    Toast.makeText(mContext, "Clicked "+ imageAndRecipe.getTxt(), Toast.LENGTH_SHORT).show();
                    assignId(imageAndRecipe);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageAndRecipeList.size();
    }

    class RecipeDetailViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView textView1;
        ScrollView scrollView;

        public RecipeDetailViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            textView1 = itemView.findViewById(R.id.recipe_frag_list);
            recyclerView = itemView.findViewById(R.id.steps_list);
            scrollView = itemView.findViewById(R.id.scroll_view);

            recyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (mImageAndRecipeList != null){
                        recipeName = mImageAndRecipeList.get(position).getTxt();
                        Toast.makeText(mContext, "A " + recipeName, Toast.LENGTH_SHORT).show();
                        saveAdapterChosenName(recipeName);
                    }
                }
            });
        }
    }


    private void populateUI(TextView textView1, ImageAndRecipe imageAndRecipe) {

        if (imageAndRecipe != null){
            ingredients = imageAndRecipe.getmIngredients();
            steps = imageAndRecipe.getmSteps();
            recipeName = imageAndRecipe.getTxt();
            stepsAdapter = new StepsAdapter(mContext, steps, imageAndRecipe, recipeName, mTwoPane, mCallback);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(stepsAdapter);

            textView1.setText("\n");
        }

        if (ingredients != null){
            textView1.append(imageAndRecipe.getTxt() + "\n\n");
            for (int i = 0; i < ingredients.size(); i++){
                textView1.append(ingredients.get(i).getIngredient() + "\n"
                        + ingredients.get(i).getQuantity() + " "
                        + ingredients.get(i).getMeasure()  + "\n");
            }
        }
    }
}
