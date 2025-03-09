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

class CalendarActivity : AppCompatActivity() {
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter
    private lateinit var selectedDateText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Initialize database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "tasks-db"
        ).build()
        taskDao = db.taskDao()

        selectedDateText = findViewById(R.id.tvSelectedDate)
        recyclerView = findViewById(R.id.rvTasksByDate)
        calendarView = findViewById(R.id.calendarView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(androidx.recyclerview.widget.RecyclerView(this), mutableListOf()) // Initialize with an empty list
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
        lifecycleScope.launch {
            taskDao.getTasksByDate(date).observe(this@CalendarActivity) { tasks ->
                adapter.setTasks(tasks)
            }
        }
    }
}
