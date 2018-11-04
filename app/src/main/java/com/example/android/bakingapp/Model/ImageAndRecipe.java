package com.example.android.bakingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class ImageAndRecipe implements Parcelable {

    public int imageId;
    public String txt;

    public ArrayList<Ingredient> getmIngredients() {
        return mIngredients;
    }

    public void setmIngredients(ArrayList<Ingredient> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public ArrayList<Step> getmSteps() {
        return mSteps;
    }

    public void setmSteps(ArrayList<Step> mSteps) {
        this.mSteps = mSteps;
    }

    public String getTxt(){ return txt;}
    public void setTxt( String txt){ this.txt = txt;}


    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;


    public ImageAndRecipe ( int imageId, String txt, ArrayList<Ingredient> ingredients, ArrayList<Step> steps){
        this.imageId = imageId;
        this.txt = txt;
        this.mIngredients = ingredients;
        this.mSteps = steps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.imageId);
        dest.writeString(this.txt);
        dest.writeTypedList(this.mIngredients);
        dest.writeList(this.mSteps);
    }

    protected ImageAndRecipe(Parcel in) {
        this.imageId = in.readInt();
        this.txt = in.readString();
        this.mIngredients = in.createTypedArrayList(Ingredient.CREATOR);
        this.mSteps = new ArrayList<Step>();
        in.readList(this.mSteps, Step.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImageAndRecipe> CREATOR = new Parcelable.Creator<ImageAndRecipe>() {
        @Override
        public ImageAndRecipe createFromParcel(Parcel source) {
            return new ImageAndRecipe(source);
        }

        @Override
        public ImageAndRecipe[] newArray(int size) {
            return new ImageAndRecipe[size];
        }
    };
}