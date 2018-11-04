package com.example.android.bakingapp.Activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.bakingapp.Model.ImageAndRecipe;
import com.example.android.bakingapp.Model.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Model.Recipe;
import com.example.android.bakingapp.Utils.RecipeInterface;
import com.example.android.bakingapp.Utils.RetrofitBuilder;
import com.example.android.bakingapp.Model.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private RecyclerView recipeView;
    private TextView loading;
    private LinearLayout dialogBox;
    private ProgressBar progressBar;
    public static ArrayList<Recipe> recipeList;
    public static ArrayList<ImageAndRecipe> imageAndRecipes;
    private RecipeListAdapter recipeAdapter;
    private GridLayoutManager layoutManager;
    private String jsonResultConvertedToString;
    private SharedPreferences sharedPreferences;
    public static final String JSON_KEY = "JSON_OBJECT_CONVERTED_TO_STRING";
    private final String RECIPE_DATA = "RECIPE_DATA";
    public static final String UPDATE_INGREDIENTS_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String EXTRA_ID = "a";
    private String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/" +
            "April/58ffdb72_5-mix-vanilla-cream-together-cheesecake/" +
            "5-mix-vanilla-cream-together-cheesecake.mp4";



    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;


    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        jsonResultConvertedToString = sharedPreferences.getString(JSON_KEY, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Recipe>>(){}.getType();
        recipeList = gson.fromJson(jsonResultConvertedToString, type);
        outState.putParcelableArrayList(RECIPE_DATA, recipeList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_master_list);

        getIdlingResource();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        recipeList = new ArrayList<>();
        imageAndRecipes = new ArrayList<>();
        ArrayList<Step> steps = new ArrayList<>();
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        recipeView = findViewById(R.id.recipe_recycler_view);
        dialogBox = findViewById(R.id.dialog);
        loading = findViewById(R.id.loading);
        progressBar = findViewById(R.id.progress_bar);


        recipeAdapter = new RecipeListAdapter(imageAndRecipes, this, mTwoPane);

        if ((findViewById(R.id.recipe_recycler_view_tablet) != null)){
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        if (savedInstanceState != null){
            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_DATA);

            if (recipeList != null) {
                imageAndRecipes.add(new ImageAndRecipe( R.drawable.nutella_pie,
                        recipeList.get(0).getName(), recipeList.get(0).getIngredients(),
                        recipeList.get(0).getSteps() ));
                imageAndRecipes.add(new ImageAndRecipe( R.drawable.brownies,
                        recipeList.get(1).getName(), recipeList.get(1).getIngredients(),
                        recipeList.get(1).getSteps() ));
                imageAndRecipes.add(new ImageAndRecipe( R.drawable.yellow_cake,
                        recipeList.get(2).getName(), recipeList.get(2).getIngredients(),
                        recipeList.get(2).getSteps() ));
                imageAndRecipes.add(new ImageAndRecipe( R.drawable.cheese_cake,
                        recipeList.get(3).getName(), recipeList.get(3).getIngredients(),
                        recipeList.get(3).getSteps() ));

                if (mTwoPane){
                    layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                    recipeView.setLayoutManager(layoutManager);
                    recipeView.setHasFixedSize(true);
                    recipeView.setAdapter(recipeAdapter);

                    DividerItemDecoration decoration = new DividerItemDecoration(this, VERTICAL);
                    recipeView.addItemDecoration(decoration);

                    DividerItemDecoration decoration2 = new DividerItemDecoration(this, HORIZONTAL);
                    recipeView.addItemDecoration(decoration2);
                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recipeView.setLayoutManager(linearLayoutManager);
                    recipeView.setHasFixedSize(true);
                    recipeView.setAdapter(recipeAdapter);

                }
            }
        }


        if(getIntent().getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID) != null){
            //Toast.makeText(this, "The widget is here", Toast.LENGTH_SHORT).show();
            jsonResultConvertedToString = sharedPreferences.getString(JSON_KEY, "");
            if (jsonResultConvertedToString.equals("")){
                if (isNetworkAvailable()){
                    downloadJson(mIdlingResource);
                } else {
                    networkDialog();
                }
            } else {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Recipe>>(){}.getType();
                recipeList = gson.fromJson(jsonResultConvertedToString, type);
            }
        }

        if (savedInstanceState == null){
            jsonResultConvertedToString = sharedPreferences.getString(JSON_KEY, "");
            if (jsonResultConvertedToString.equals("")){
                if (isNetworkAvailable()){
                    downloadJson(mIdlingResource);
                } else {
                    networkDialog();
                }
            } else {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Recipe>>(){}.getType();
                recipeList = gson.fromJson(jsonResultConvertedToString, type);
                if (recipeList != null) {
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.nutella_pie,
                            recipeList.get(0).getName(), recipeList.get(0).getIngredients(),
                            recipeList.get(0).getSteps() ));
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.brownies,
                            recipeList.get(1).getName(), recipeList.get(1).getIngredients(),
                            recipeList.get(1).getSteps() ));
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.yellow_cake,
                            recipeList.get(2).getName(), recipeList.get(2).getIngredients(),
                            recipeList.get(2).getSteps() ));
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.cheese_cake,
                            recipeList.get(3).getName(), recipeList.get(3).getIngredients(),
                            recipeList.get(3).getSteps() ));

                    if (mTwoPane){
                        dialogBox.setVisibility(View.VISIBLE );
                        progressBar.setVisibility(View.VISIBLE );
                        loading.setVisibility(View.VISIBLE );

                        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                        recipeView.setLayoutManager(layoutManager);
                        recipeView.setHasFixedSize(true);
                        recipeView.setAdapter(recipeAdapter);

                        DividerItemDecoration decoration = new DividerItemDecoration(this, VERTICAL);
                        recipeView.addItemDecoration(decoration);

                        DividerItemDecoration decoration2 = new DividerItemDecoration(this, HORIZONTAL);
                        recipeView.addItemDecoration(decoration2);
                    } else {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                        recipeView.setLayoutManager(linearLayoutManager);
                        recipeView.setHasFixedSize(true);
                        recipeView.setAdapter(recipeAdapter);
                    }
                }
            }
        }
    }

    public void downloadJson(final SimpleIdlingResource mIdlingResource) {
        mIdlingResource.setIdleState(false);
        RecipeInterface recipeInterface = RetrofitBuilder.Retrieve();
        Call<ArrayList<Recipe>> recipe = recipeInterface.getRecipe();

        dialogBox.setVisibility(View.VISIBLE );
        progressBar.setVisibility(View.VISIBLE );
        loading.setVisibility(View.VISIBLE );

        recipe.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                Context context = getApplicationContext();
                ArrayList<Recipe> recipes = response.body();
                recipeList = recipes;
                Gson gson = new Gson();
                jsonResultConvertedToString = gson.toJson(response.body());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(JSON_KEY, jsonResultConvertedToString);
                editor.commit();

                if (recipes != null) {
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.nutella_pie,
                            recipes.get(0).getName(), recipes.get(0).getIngredients(),
                            recipes.get(0).getSteps() ));
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.brownies,
                            recipes.get(1).getName(), recipes.get(1).getIngredients(),
                            recipes.get(1).getSteps() ));
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.yellow_cake,
                            recipes.get(2).getName(), recipes.get(2).getIngredients(),
                            recipes.get(2).getSteps() ));
                    imageAndRecipes.add(new ImageAndRecipe( R.drawable.cheese_cake,
                            recipes.get(3).getName(), recipes.get(3).getIngredients(),
                            recipes.get(3).getSteps() ));
                }

                if ((findViewById(R.id.recipe_recycler_view_tablet) != null)){
                    mTwoPane = true;
                } else {
                    mTwoPane = false;
                }


                Bundle recipesBundle = new Bundle();
                recipesBundle.putParcelableArrayList("AllRecipes", recipes);
                recipeAdapter.setRecipeData(imageAndRecipes, context , mTwoPane);
                mIdlingResource.setIdleState(true);
                if (mTwoPane){
                    layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                    recipeView.setLayoutManager(layoutManager);
                    recipeView.setHasFixedSize(true);
                    recipeView.setAdapter(recipeAdapter);

                    DividerItemDecoration decoration = new DividerItemDecoration(context, VERTICAL);
                    recipeView.addItemDecoration(decoration);

                    DividerItemDecoration decoration2 = new DividerItemDecoration(context, HORIZONTAL);
                    recipeView.addItemDecoration(decoration2);
                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    recipeView.setLayoutManager(linearLayoutManager);
                    recipeView.setHasFixedSize(true);
                    recipeView.setAdapter(recipeAdapter);
                }

                dialogBox.setVisibility(View.INVISIBLE );
                progressBar.setVisibility(View.INVISIBLE );
                loading.setVisibility(View.INVISIBLE );
            }
            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.v("http fail: ", t.getMessage());

                networkDialog();
                dialogBox.setVisibility(View.INVISIBLE );
                progressBar.setVisibility(View.INVISIBLE );
                loading.setVisibility(View.INVISIBLE );
            }
        });
    }


    /**
     * This method checks if there is network connection
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager con = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = con.getActiveNetworkInfo();
        return  activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * networkDialog() displays a dialog if network is not available
     */
    private void networkDialog() {
        dialogBox.setVisibility(View.VISIBLE );
        progressBar.setVisibility(View.VISIBLE );
        loading.setVisibility(View.VISIBLE );

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Network Unavailable, turn on WIFI or Mobile data");
        alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (isNetworkAvailable()){
                    downloadJson(mIdlingResource);
                    dialogBox.setVisibility(View.INVISIBLE );
                    progressBar.setVisibility(View.INVISIBLE );
                    loading.setVisibility(View.INVISIBLE );
                } else {
                    networkDialog();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogBox.setVisibility(View.INVISIBLE );
                progressBar.setVisibility(View.INVISIBLE );
                loading.setVisibility(View.INVISIBLE );

                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }



    /*********************************************THE ADAPTER PORTION USED BY THE ACTIVITY TO BIND THE RECYCLER VIEW*******************************/


    /**
     * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
     */

    public static class RecipeListAdapter extends RecyclerView.Adapter<MainActivity.RecipeListAdapter.RecipeViewHolder> {

        List<ImageAndRecipe> imageAndRecipesList = Collections.emptyList();
        public static ImageAndRecipe imageRecipe;
        private boolean mTwoPane;
        Context mContext;


        private final View.OnClickListener mOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                imageRecipe = (ImageAndRecipe) view.getTag();
                Context context = view.getContext();
                //Toast.makeText(mContext, "Adapter", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putParcelable("ImageAndRecipeObject", imageRecipe);
                Intent intent = new Intent(context, FragmentActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };

        public RecipeListAdapter(List<ImageAndRecipe> imageAndRecipesList, Context context, boolean twoPane){
            this.imageAndRecipesList = imageAndRecipesList;
            this.mContext = context;
            this.mTwoPane = twoPane;
        }

        public void setRecipeData(List<ImageAndRecipe> imageAndRecipesList, Context context, boolean twoPane) {
            this.imageAndRecipesList = imageAndRecipesList;
            this.mContext = context;
            this.mTwoPane = twoPane;
            notifyDataSetChanged();
        }

        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.recipe_list;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            RecipeViewHolder viewHolder = new RecipeViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecipeViewHolder holder, int position) {
            imageRecipe = imageAndRecipesList.get(position);
            holder.name.setText(imageRecipe.txt);
            holder.imageView.setImageResource(imageRecipe.imageId);

            holder.itemView.setTag(imageRecipe);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return imageAndRecipesList.size();
        }

        public class RecipeViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView name;

            public RecipeViewHolder(View itemView){
                super (itemView);

                imageView = itemView.findViewById(R.id.recipe_picture);
                name = itemView.findViewById(R.id.recipe);
            }
        }
    }
}