package com.example.android.bakingapp.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.bakingapp.Activities.MainActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Services.IngredientsService2;

/**
 * Created by Soyombo Soyinka O. Johnson on 9/16/2018. Udacity Android Developer Nanodegree Term 1
 */

public class RecipeWidgetProvider extends AppWidgetProvider {
    public static final String DATA_FETCHED = "com.example.android.bakingapp.DATA_FETCHED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            Intent serviceIntent = new Intent(context, IngredientsService2.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.stack_widget_view, serviceIntent);
            remoteViews.setEmptyView(R.id.stack_widget_view, R.id.empty_stack_view_text);

            Intent viewIntent = new Intent(context, MainActivity.class);
            viewIntent.setAction(MainActivity.UPDATE_INGREDIENTS_ACTION);
            viewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            viewIntent.setData(Uri.parse(viewIntent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);
            remoteViews.setPendingIntentTemplate(R.id.stack_widget_view, viewPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

}
