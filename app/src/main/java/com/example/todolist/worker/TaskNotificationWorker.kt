package com.example.todolist.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.TaskDao
import com.example.todolist.utils.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TaskNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val taskDao: TaskDao by lazy {
        AppDatabase.getDatabase(applicationContext).taskDao()
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Get tomorrow's date in "yyyy-MM-dd" format
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val tomorrowDate = dateFormat.format(calendar.time)

                // Fetch tasks due tomorrow
                val tasksDueTomorrow = taskDao.getTasksByDate2(tomorrowDate)

                if (tasksDueTomorrow.isNotEmpty()) {
                    // Show notification
                    val notificationHelper = NotificationHelper(applicationContext)
                    val title = "Tasks Due Tomorrow"
                    val message = "You have ${tasksDueTomorrow.size} task(s) due tomorrow."
                    notificationHelper.showNotification(title, message)
                }

                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}