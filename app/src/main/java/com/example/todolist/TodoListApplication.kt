package com.example.todolist

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todolist.worker.TaskNotificationWorker
import java.util.concurrent.TimeUnit

class TodoListApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleTaskNotificationWorker()
    }

    private fun scheduleTaskNotificationWorker() {
        val workRequest = PeriodicWorkRequestBuilder<TaskNotificationWorker>(
            24, // Repeat every 24 hours
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "TaskNotificationWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}