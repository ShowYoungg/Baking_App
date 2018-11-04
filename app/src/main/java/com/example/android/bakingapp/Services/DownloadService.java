package com.example.android.bakingapp.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingapp.Fragments.StepsFragment;
import com.example.android.bakingapp.Model.Download;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Utils.RecipeInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class DownloadService extends IntentService {

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;
    private String nameOfRecipe;

    private File outputFile;
    private boolean writtenToDisk;
    Context context;

    public static final String ACTION_STEP = "com.example.android.bakingapp.action.step";
    public static final String EXTRA_STEP_ID = "com.example.android.bakingapp.extra.STEP_ID";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DownloadService() {
        super("DownloadService");
    }

    public static void sendRecipeData(Context context, String recipeName, String urlLink) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_STEP);
        intent.putExtra(EXTRA_STEP_ID, recipeName);
        intent.putExtra("URL", urlLink);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_STEP.equals(action)) {
                String recipeName = intent.getStringExtra(EXTRA_STEP_ID);
                String urlLink = intent.getStringExtra("URL");
                downloadMediaAssets(urlLink, recipeName);
            }
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * This method downloads the media assets from the network link fetched from the json
     *
     * @Param: String url.
     */

    private void downloadMediaAssets(String url, String name) {
        nameOfRecipe = name;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/topher/2017/April/");
        Retrofit retrofit = builder.build();
        RecipeInterface recipeInterface = retrofit.create(RecipeInterface.class);
        Call<ResponseBody> call = recipeInterface.downloadMedia(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {

                            saveDownloadedMediaFile(response.body(), nameOfRecipe);
                            Log.i("TAG", "is file download successful? " + writtenToDisk);

                            return null;
                        }
                    }.execute();

                } else {
                    Toast.makeText(context,
                            "Error downloading file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


//    //this method returns true if there is active network connection
//    public boolean isNetworkAvailable() {
//        ConnectivityManager con = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = con.getActiveNetworkInfo();
//        return  activeNetwork != null && activeNetwork.isConnected();
//    }


    private void saveDownloadedMediaFile(ResponseBody body, String fileName) {

        try{
            int count;
            byte data[] = new byte[1024 * 4];
            long fileSize = body.contentLength();
            long total = 0;
            long startTime = System.currentTimeMillis();
            int timeCount = 1;
            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);

            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/BakeMedia/");
            dir.mkdirs();
            outputFile = new File(dir + "/" + fileName + ".mp4/");
            OutputStream output = new FileOutputStream(outputFile);

            while ((count = bis.read(data)) != -1) {

                total += count;
                totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
                double current = Math.round(total / (Math.pow(1024, 2)));

                int progress = (int) ((total * 100) / fileSize);

                long currentTime = System.currentTimeMillis() - startTime;

                Download download = new Download();
                download.setTotalFileSize(totalFileSize);

                if (currentTime > 1000 * timeCount) {
                    download.setCurrentFileSize((int) current);
                    download.setProgress(progress);
                    sendNotification(download);
                    timeCount++;
                }

                output.write(data, 0, count);
            }
            onDownloadComplete();
            output.flush();
            output.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Download download) {

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(String.format("Downloaded (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent(Download download) {

        Intent intent = new Intent(StepsFragment.MESSAGE_PROGRESS);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }



}
