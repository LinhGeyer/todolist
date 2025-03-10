package com.example.todolist

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.TaskDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.todolist.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarActivity : AppCompatActivity() {
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter
    private lateinit var selectedDateText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarView: CalendarView
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Initialize database asynchronously
        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "tasks-db"
            ).build()
            taskDao = db.taskDao()

            withContext(Dispatchers.Main) {
                // Initialize UI components after database is ready
                initializeUI()
            }
        }
    }

    private fun initializeUI() {
        selectedDateText = findViewById(R.id.tvSelectedDate)
        recyclerView = findViewById(R.id.rvTasksByDate)
        calendarView = findViewById(R.id.calendarView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(taskDao) // Initialize with an empty list
        recyclerView.adapter = adapter

        // Set default date to today
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())
        selectedDateText.text = "Tasks for: $todayDate"
        loadTasksForDate(todayDate)

        // Listen for date changes
        calendarView.setOnDateChangeListener { _, year, month, day ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            selectedDateText.text = "Tasks for: $selectedDate"
            loadTasksForDate(selectedDate)
        }
    }

    private fun loadTasksForDate(date: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val tasks = taskDao.getTasksByDate2(date) // Use the non-LiveData version
            withContext(Dispatchers.Main) {
                adapter.setTasks(tasks)
            }
        }
    }

    // Call this method after adding a new task to refresh the list
    fun refreshTaskList() {
        loadTasksForDate(selectedDate)
    }
}