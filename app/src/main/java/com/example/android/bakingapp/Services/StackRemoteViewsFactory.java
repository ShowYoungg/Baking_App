package com.example.android.bakingapp.Services;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.Activities.MainActivity;
import com.example.android.bakingapp.Fragments.RecipeDetailFragment;
import com.example.android.bakingapp.Model.Ingredient;
import com.example.android.bakingapp.Model.Recipe;
import com.example.android.bakingapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private Context mContext;
    private int mAppWidgetId;
    public static ArrayList<Recipe> recipeLists;
    private final String JSON_KEY = "JSON_OBJECT_CONVERTED_TO_STRING";
    public static int recipeNameId;



    public StackRemoteViewsFactory(Context context, Intent intent){
      mContext = context;
      mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        recipeLists = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        String jsonResultConvertedToString = sharedPreferences.getString(JSON_KEY, "");

        //SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences();
        recipeNameId = sharedPreferences.getInt(RecipeDetailFragment.RECIPE_NAME, -1);

        Gson gson = new Gson();
        Type type = new TypeToken<List<Recipe>>(){}.getType();
        recipeLists = gson.fromJson(jsonResultConvertedToString, type);
    }

    @Override
    public void onDestroy() {
        recipeLists.clear();
    }

    @Override
    public int getCount() {
        if (recipeLists != null){
            return recipeLists.size();
        } else {
            return 1;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews( mContext.getPackageName(), R.layout.widget_list);
        ArrayList<Integer> list = new ArrayList<>();
        getUpdate(recipeNameId);

        //The switch statement re orders the list so that the StackView can display the
        // desired recipe first before displaying others
        switch (recipeNameId){
            case 0:
                list.add(0);
                list.add(1);
                list.add(2);
                list.add(3);
                break;
            case 1:
                list.add(1);
                list.add(0);
                list.add(2);
                list.add(3);
                break;
            case 2:
                list.add(2);
                list.add(1);
                list.add(0);
                list.add(3);
                break;
            case 3:
                list.add(3);
                list.add(1);
                list.add(2);
                list.add(0);
                break;
            default:
                list.add(0);
                list.add(1);
                list.add(2);
                list.add(3);
                break;
        }

        if (position <= getCount()){
            if (recipeLists != null){
                Log.i("RecipeNameId", "This is the id " + recipeNameId);

                Recipe recipe = recipeLists.get(position);
                StringBuilder s = new StringBuilder();
                ArrayList<Ingredient> ingredients = recipeLists.get(list.get(position)).getIngredients();
                if (ingredients != null){
                    s.append(recipeLists.get(list.get(position)).getName() + "\n\n");
                    for (int j = 0; j < ingredients.size(); j++){
                        s.append(ingredients.get(j).getIngredient() + ": "
                                + ingredients.get(j).getQuantity() + " "
                                + ingredients.get(j).getMeasure()  + "\n");
                    }
                    s.append("\n");
                }
                String listOfIngredients = s.toString();
                rv.setTextViewText(R.id.widget_text_view1, listOfIngredients);

                Bundle extras = new Bundle();
                extras.putInt(MainActivity.EXTRA_ID, recipe.getId());
                Intent fillIntent = new Intent();
                fillIntent.putExtras(extras);
                rv.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);
            }
        }
        return rv;
    }


    public static void getUpdate( int id ){
        id = recipeNameId;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
