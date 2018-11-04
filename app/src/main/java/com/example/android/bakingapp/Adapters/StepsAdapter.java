package com.example.android.bakingapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.bakingapp.Activities.StepsActivity;
import com.example.android.bakingapp.Model.ImageAndRecipe;
import com.example.android.bakingapp.Model.Step;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Fragments.RecipeDetailFragment;

import java.util.ArrayList;

/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepAdapterViewHolder> {

    private boolean mTwoPane;
    private ImageAndRecipe imageAndRecipe;
    private ArrayList<Step> stepArrayList;
    private String mRecipeName;
    private Context mContext;

    //RecipeDetailFragment.OnStepListClickListener mCallback;
    RecipeDetailFragment.OnStepListClickListener mCallback;


    public StepsAdapter(Context context, ArrayList<Step> steps, ImageAndRecipe imageRecipe,
                        String recipeName, boolean twoPane, RecipeDetailFragment.OnStepListClickListener listener) {

        this.mContext = context;
        this.stepArrayList = steps;
        this.imageAndRecipe = imageRecipe;
        this.mRecipeName = recipeName;
        this.mTwoPane = twoPane;
        this.mCallback = listener;
        notifyDataSetChanged();
    }

    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.steps_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        StepsAdapter.StepAdapterViewHolder viewHolder = new StepsAdapter.StepAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder holder, int position) {

        final Step k = stepArrayList.get(position);
        holder.step.setText(k.getShortDescription());

        holder.step.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mTwoPane){
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("StepObject", k);
                        Intent intent = new Intent( mContext, StepsActivity.class);
                        intent.putExtras(bundle);
                        intent.putExtra("Name", mRecipeName);
                        mContext.startActivity(intent);
                    } else {
                         int clickedPosition = 0 ;
                         if (mCallback != null){
                            mCallback.onStepListSelected(k, clickedPosition, imageAndRecipe);
                         }
                    }
                }
        });

    }

    @Override
    public int getItemCount() {
        return stepArrayList.size();
    }


    class StepAdapterViewHolder extends RecyclerView.ViewHolder {

        Button step;


        public StepAdapterViewHolder(View itemView) {
            super(itemView);

           step = itemView.findViewById(R.id.steps_button);
        }
    }
}