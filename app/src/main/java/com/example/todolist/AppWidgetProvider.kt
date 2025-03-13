package com.example.todolist

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class TodoListWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Schedule a Worker to fetch tasks
        val workRequest = OneTimeWorkRequest.Builder(FetchTasksWorker::class.java).build()
        WorkManager.getInstance(context).enqueue(workRequest)

        Log.d("TodoListWidget", "Scheduled FetchTasksWorker")

        // Update all instances of the widget
        for (appWidgetId in appWidgetIds) {
            // Create RemoteViews object and set the layout
            val views = RemoteViews(context.packageName, R.layout.todoapp_todo_list_widget)

            // Set up the Refresh button's onClick functionality
            val refreshIntent = Intent(context, TodoListWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_button, refreshPendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}