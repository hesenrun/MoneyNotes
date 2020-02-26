package com.bqmz001.moneynotes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bqmz001.moneynotes.EditNoteActivity;
import com.bqmz001.moneynotes.MainActivity;
import com.bqmz001.moneynotes.R;

/**
 * Implementation of App Widget functionality.
 */
public class QuickAddWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quick_add);
        Intent fullIntent = new Intent(context, EditNoteActivity.class);
        fullIntent.putExtra("note_id", -1);
        fullIntent.putExtra("from", "widget");
        PendingIntent Pfullintent = PendingIntent.getActivity(context, 0, fullIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.quick_add, Pfullintent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quick_add);
        Intent fullIntent = new Intent(context, EditNoteActivity.class);
        fullIntent.putExtra("note_id", -1);
        fullIntent.putExtra("from", "widget");
        PendingIntent Pfullintent = PendingIntent.getActivity(context, 0, fullIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.quick_add, Pfullintent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(new ComponentName(context, QuickAddWidget.class), views);
    }
}

