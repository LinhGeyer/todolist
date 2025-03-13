package com.example.todolist

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolist.data.AppDatabase
import com.example.todolist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchTasksWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Fetch tasks from the database
            val tasks = withContext(Dispatchers.IO) {
                fetchTasksFromDatabase(applicationContext)
            }

            Log.d("FetchTasksWorker", "Fetched ${tasks.size} tasks")

            // Update the widget with the fetched tasks
            updateWidget(applicationContext, tasks)

            Result.success()
        } catch (e: Exception) {
            Log.e("FetchTasksWorker", "Error fetching tasks", e)
            Result.failure()
        }
    }

    private fun updateWidget(context: Context, tasks: List<Task>) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, TodoListWidget::class.java)
        )

        Log.d("FetchTasksWorker", "Updating ${appWidgetIds.size} widget instances")

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.todoapp_todo_list_widget)

            // Display tasks or a placeholder message
            val taskListText = if (tasks.isEmpty()) {
                "No tasks found"
            } else {
                tasks.joinToString("\n") { it.title }
            }
            views.setTextViewText(R.id.widget_task_list, taskListText)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Notify the AppWidgetManager that the data has changed
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_task_list)
    }

    private fun fetchTasksFromDatabase(context: Context): List<Task> {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "tasks-db"
        ).build()

        return db.taskDao().getAllTasks1()
    }
}