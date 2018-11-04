package com.example.android.bakingapp.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.Activities.MainActivity;
import com.example.android.bakingapp.Model.Download;
import com.example.android.bakingapp.Services.DownloadService;
import com.example.android.bakingapp.Model.ImageAndRecipe;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Model.Recipe;
import com.example.android.bakingapp.Model.Step;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.Activities.MainActivity.JSON_KEY;

/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class StepsFragment extends Fragment {

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Step step;
    private String urlLink;
    private String streamingUrlLink;
    private TextView textView;
    private String description;
    private ImageAndRecipe imageAndRecipe;
    private ArrayList<Step> stepList;
    private String recipeName;
    private ArrayList<Recipe> recipeLists;
    private File file;
    private Uri downloadPath;
    private Download download;
    private String pathName;
    private boolean writtenToDisk;
    private int stepId;
    private int i;
    private Uri SAMPLE;
    private boolean mLandscape;
    private long fileSize;
    private long fileSizeDownloaded;
    private SharedPreferences stepDetailPreferences;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;


    private void getSavedJson() {
        String s = sharedPreferences.getString(JSON_KEY,"");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Recipe>>(){}.getType();
        recipeLists = gson.fromJson(s, type);
        if (recipeName != null){
            if (recipeName.equals("Nutella Pie") ){
                stepList = recipeLists.get(0).getSteps();
            }
            if (recipeName.equals("Brownies") ){
                stepList = recipeLists.get(1).getSteps();
            }
            if (recipeName.equals("Yellow Cake") ){
                stepList = recipeLists.get(2).getSteps();
            }
            if (recipeName.equals("Cheesecake") ){
                stepList = recipeLists.get(3).getSteps();
            }
        }
    }

    public static final String MESSAGE_PROGRESS = "Notification";

    public StepsFragment(){
    }


    /**
     * This saves the state of recipeName upon configuration change,
     * the value assigned to recipeName does not get thrown away
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("StepState", step);
        outState.putString("RecipeName", recipeName);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is the equivalent of onCreate() for activity.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        stepDetailPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //Create navigation arrow only when on phone,
        // not on tablets because tablets will get theirs from FragmentActivity
        if (rootView.findViewById(R.id.scrollView) != null){
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        stepList = new ArrayList<>();
        recipeLists = new ArrayList<>();

        playerView = rootView.findViewById(R.id.player_view);
        textView = rootView.findViewById(R.id.player_view_description);

        final BottomNavigationView navigation = rootView.findViewById(R.id.navigation);

        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (navigation.getVisibility() == View.INVISIBLE){
                        navigation.setVisibility(View.VISIBLE);
                    } else {
                        navigation.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null){
            if (stepDetailPreferences != null){
                description = stepDetailPreferences.getString("Description", description);
                textView.setText(description);
                streamingUrlLink = stepDetailPreferences.getString("StreamingLink", streamingUrlLink);
                recipeName = stepDetailPreferences.getString("RecipeName", recipeName);
                stepId = stepDetailPreferences.getInt("StepId", stepId);
            } else if (getArguments() != null){
                step = getArguments().getParcelable("RECIPE_STEP");
                recipeName = getArguments().getString("NAME_OF_RECIPE");
                if (recipeName == null){
                    recipeName = savedInstanceState.getString("RecipeName");
                }
                if (step != null){
                    //Toast.makeText(getContext(), "mTwoPAne1 " + recipeName, Toast.LENGTH_SHORT).show();
                    description = step.getDescription();
                    textView.setText(description);
                    streamingUrlLink = step.getVideoURL();
                    stepId = step.getId();
                }
            } else {
                step = savedInstanceState.getParcelable("StepState");
                recipeName = savedInstanceState.getString("RecipeName");
                if (step != null){
                    description = step.getDescription();
                    textView.setText(description);
                    streamingUrlLink = step.getVideoURL();
                    stepId = step.getId();
                }
            }
        }
        if (savedInstanceState == null){
            if (getArguments() != null){
                //This argument is coming from StepActivity ( or when instance state is not null)
                step = getArguments().getParcelable("StepObject");
                recipeName = getArguments().getString("Name");
                i = getArguments().getInt("ID");

                //This recipeName belongs to saved instance state
                if (recipeName == null){
                    recipeName = getArguments().getString("RecipeName");
                }

                if (step == null){
                    //Toast.makeText(getContext(), "From Callback interface", Toast.LENGTH_SHORT).show();
                    step = getArguments().getParcelable("Step");
                    recipeName = getArguments().getString("recipeName");
                    i = getArguments().getInt("ID");
                }

                if (step != null){
                    //The int (i) == -100 means this argument is coming from
                    // StepActivity when savedInstance is null
                    if (i == -100){
                        //Toast.makeText(getContext(), "step1 gotten", Toast.LENGTH_SHORT).show();
                        description = step.getDescription();
                        textView.setText(description);
                        streamingUrlLink = step.getVideoURL();
                        stepId = step.getId();
                    } else {
                        //else the int (i) will be equals to 500 if not -100, this means the argument
                        // is coming from StepActivity when savedInstance is not null
                        if (stepDetailPreferences != null){
                            description = stepDetailPreferences.getString("Description", description);
                            textView.setText(description);
                            streamingUrlLink = stepDetailPreferences.getString("StreamingLink", streamingUrlLink);
                            recipeName = stepDetailPreferences.getString("RecipeName", recipeName);
                            stepId = stepDetailPreferences.getInt("StepId", stepId);
                        }
                    }
                } else {
                    step = getArguments().getParcelable("StepObject2");
                    recipeName = getArguments().getString("name");
                    if (step != null){
                        //Toast.makeText(getContext(), "step2 gotten", Toast.LENGTH_SHORT).show();
                        description = step.getDescription();
                        textView.setText(description);
                        streamingUrlLink = step.getVideoURL();
                        stepId = step.getId();
                    } else if (stepDetailPreferences != null){
                        //when every intent and argument fail,
                        // there should be some data stored in SharedPreferences:
                        // at first onCreate() call, SharedPreferences will be null; hence,
                        // subsequently, some data are saved and then retrieved
                        description = stepDetailPreferences.getString("Description", description);
                        textView.setText(description);
                        streamingUrlLink = stepDetailPreferences.getString("StreamingLink", streamingUrlLink);
                        recipeName = stepDetailPreferences.getString("RecipeName", recipeName);
                        stepId = stepDetailPreferences.getInt("StepId", stepId);
                    } else {
                        //This will be called in first onCreate() call before some data
                        // are stored in stepDetailPreferences--SharedPreferences.
                        step = getArguments().getParcelable("RECIPE_STEP");
                        recipeName = getArguments().getString("NAME_OF_RECIPE");
                        if (step != null){
                            //Toast.makeText(getContext(), "mTwoPAne2" + recipeName, Toast.LENGTH_SHORT).show();
                            description = step.getDescription();
                            textView.setText(description);
                            streamingUrlLink = step.getVideoURL();
                            stepId = step.getId();
                        }
                    }
                }
            }
        }
        RecipeDetailFragment.saveAdapterChosenName(recipeName);
        getSavedJson();
        downloadAndSaveVideo();
        navigation.setOnNavigationItemSelectedListener(getmOnNavigationItemSelectedListener());

        return rootView;
    }


    /**
     * Downloading of media files and saving into external memory happen here
     */
    private void downloadAndSaveVideo() {
        //Initialising and referencing the downloaded file paths
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/BakeMedia/");
        dir.mkdirs();
        pathName = dir + "/" + recipeName + stepId + ".mp4/";
        file = new File(pathName);

        int fileSize = Integer.parseInt(String.valueOf(file.length()/1024));

        if (!file.exists()){
            if (streamingUrlLink != null && !streamingUrlLink.equals("")) {
                if (isNetworkAvailable()) {
                    // This method initialises broadcast receiver that start the DownloadService class
                    registerReceiver();
                    //This method gets its parameters (variables) from this context so it can be
                    // used in the DownloadService for starting a file download
                    DownloadService.sendRecipeData(getContext(),
                            recipeName + step.getId(), streamingUrlLink);
                } else {
                    Toast.makeText(getContext(), "Please enable network to download videos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //This is where ExoPlayer starts
        initialiseAndPlayVideo();
    }

    /**
     * Initialisation and playing of videos from the given url
     */
    private void initialiseAndPlayVideo() {
        if (streamingUrlLink != null){
            if (streamingUrlLink.equals("")) {
                SAMPLE = Uri.parse("https://d17h27t6h515a5.cloudfront.net/topher/2017" +
                        "/April/58ffdb72_5-mix-vanilla-cream-together-cheesecake" +
                        "/5-mix-vanilla-cream-together-cheesecake.mp4");
            } else {
               if (file.exists()) {
                    SAMPLE = Uri.parse(pathName);
                } else {
                    SAMPLE = Uri.parse(streamingUrlLink);
                }
            }
        }

        player = ExoPlayerFactory.newSimpleInstance( getContext(), new DefaultTrackSelector());
        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        DefaultDataSourceFactory dataSourceFactory =
                new DefaultDataSourceFactory(getContext(),
                        Util.getUserAgent(getContext(), "bakingapp"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(SAMPLE);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }


    /**
     * at onStop(), Exoplayer is released and resources cleaned up
     */
    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = stepDetailPreferences.edit();
        editor.putString("Description", description);
        editor.putString("StreamingLink", streamingUrlLink);
        editor.putString("RecipeName", recipeName);
        editor.putInt("StepId", stepId);
        editor.commit();


        playerView.setPlayer(null);
        player.release();
        player = null;
    }


    /**
     * This checks network availability, it returns true if network is available and false if not
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = con.getActiveNetworkInfo();
        return  activeNetwork != null && activeNetwork.isConnected();
    }


    /**
     * This method registers broadcast receiver
     */
    private void registerReceiver(){

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }


    /**
     * The BroadcastReceiver gets the intent from DownloadService
     * and broadcast the content of the intent
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MESSAGE_PROGRESS)){
                //Download download = intent.getParcelableExtra("download");
                download = intent.getParcelableExtra("download");
                if(download.getProgress() == 2){
                    Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();
                }

                if(download.getProgress() == 100){
                    Toast.makeText(context, "File Download Complete", Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        }
    };

    /**
     * This method verifies the completion of the download, if download does not complete,
     * the file should be deleted from storage
     * @param download
     */
    private void verifyCompleteDownload(Download download){
        boolean deleteFile = false;
        if (download != null){
            int progress = download.getProgress();
            int fileSize = download.getTotalFileSize();

            if (progress != fileSize && !isNetworkAvailable() && fileSize >=0){
                if (file.exists()){
                    deleteFile = file.delete();
                }
            }

            if (deleteFile){
                Toast.makeText(getContext(), "File Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //if any file does not download completely, delete such file
        if (download != null){
            verifyCompleteDownload(download);
        }
    }


    /**
     * The bottom navigation bar and the views therein are controlled here
     * i.e if back arrow is clicked, if the forward arrow and home views are clicked; some actions
     * defined herein should execute
     * @return boolean
     */
    public BottomNavigationView.OnNavigationItemSelectedListener getmOnNavigationItemSelectedListener() {
        return mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_navigation:

                        startActivity(new Intent( getContext(), MainActivity.class));
                        return true;
                    case R.id.back_navigation:

                        if (stepList != null && stepId != 0){

                            playerView.setPlayer(null);
                            player.release();
                            player = null;

                            if (stepId - 1 == 7 && recipeName.equals("Yellow Cake")){
                                step = stepList.get(6);
                            } else {
                                step = stepList.get(stepId - 1);
                            }

                            description = step.getDescription();
                            textView.setText(description);
                            streamingUrlLink = step.getVideoURL();
                            stepId = step.getId();

                            downloadAndSaveVideo();
                            initialiseAndPlayVideo();
                        }

                        return true;
                    case R.id.next_navigation:

                        if (stepList != null && stepId >=0){
                            int index = stepList.size();
                            if (stepId + 1 != index){

                                playerView.setPlayer(null);
                                player.release();
                                player = null;


                                if (stepId == 8 && recipeName.equals("Yellow Cake")){
                                    step = stepList.get(stepId);
                                } else {
                                    step = stepList.get(stepId + 1);
                                }

                                if (step != null){
                                    description = step.getDescription();
                                    textView.setText(description);
                                    streamingUrlLink = step.getVideoURL();
                                    stepId = step.getId();

                                    downloadAndSaveVideo();
                                    initialiseAndPlayVideo();
                                }
                            } else {
                                //do not increment
                            }
                        }
                        return true;
                }
                return false;
            }
        };
    }
}
