package com.example.todolist.worker

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolist.data.AppDatabase
import com.example.todolist.model.Task
import com.example.todolist.utils.NotificationHelper
import java.util.Calendar

class TaskNotificationWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Check for tasks due the next day
        val tasksDueTomorrow = getTasksDueTomorrow()

        if (tasksDueTomorrow.isNotEmpty()) {
            // Send a notification
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.sendNotification(
                "Tasks Due Tomorrow",
                "You have ${tasksDueTomorrow.size} task(s) due tomorrow."
            )
        }

        return Result.success()
    }

    private suspend fun getTasksDueTomorrow(): List<Task> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Tomorrow
        val tomorrow = calendar.time

        // Query your database for tasks due tomorrow
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "tasks-db"
        ).build()

        return db.taskDao().getTasksByDate2(tomorrow.toString()) // Convert date to String if needed
    }
}