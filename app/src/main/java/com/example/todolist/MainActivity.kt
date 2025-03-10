package com.example.todolist

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.TaskDao
import com.example.todolist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter
    private lateinit var spinnerCategory: Spinner
    private lateinit var taskInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var taskDateInput: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        taskInput = findViewById(R.id.etNewTask)
        categoryInput = findViewById(R.id.etTaskCategory)
        taskDateInput = findViewById(R.id.etTaskDate)
        val addTaskButton = findViewById<Button>(R.id.btnAddTask)
        val recyclerView = findViewById<RecyclerView>(R.id.rvTasks)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        // Initialize Room Database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "tasks-db"
        )
            .fallbackToDestructiveMigration()
            .build()
        taskDao = db.taskDao()

        // Initialize RecyclerView
        adapter = TaskAdapter(taskDao)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load all tasks sorted by category
        loadTasks()

        // Set up date picker for taskDateInput
        taskDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format selected date as "YYYY-MM-DD"
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                taskDateInput.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        // Handle task addition
        addTaskButton.setOnClickListener {
            val taskTitle = taskInput.text.toString().trim()
            val taskCategory = categoryInput.text.toString().trim()
            val taskDate = taskDateInput.text.toString().trim()

            if (taskTitle.isNotBlank() && taskDate.isNotBlank()) {
                val category = if (taskCategory.isNotBlank()) taskCategory else "General"
                val newTask = Task(title = taskTitle, isCompleted = false, category = category, date = taskDate)

                lifecycleScope.launch(Dispatchers.IO) {
                    taskDao.insertTask(newTask)
                }

                // Clear input fields
                taskInput.text.clear()
                categoryInput.text.clear()
                taskDateInput.text.clear()
            } else {
                Toast.makeText(this, "Please enter a task and select a date", Toast.LENGTH_SHORT).show()
            }
        }

        // Open Calendar Activity
        findViewById<Button>(R.id.btnOpenCalendar).setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        // Handle category selection for filtering
        setupCategoryFilter()
    }

    private fun loadTasks(selectedCategory: String? = null) {
        if (selectedCategory == null || selectedCategory == "All") {
            // Observe all tasks
            taskDao.getAllTasks().observe(this, Observer { tasks ->
                adapter.setTasks(tasks)
            })
        } else {
            // Observe tasks by category
            taskDao.getTasksByCategory(selectedCategory).observe(this, Observer { tasks ->
                adapter.setTasks(tasks)
            })
        }
    }

    private fun setupCategoryFilter() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = taskDao.getAllCategories()
            val categoryList = mutableListOf("All") + categories

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryList
                )
                spinnerCategory.adapter = adapter

                spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedCategory = categoryList[position]
                        loadTasks(selectedCategory)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle the case where nothing is selected (optional)
                    }
                }
            }
        }
    }
}