package com.example.todolist

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.TaskDao
import com.example.todolist.model.Task
import kotlinx.coroutines.*
import android.view.View
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter
    private lateinit var spinnerCategory: Spinner
    private lateinit var taskInput: EditText
    private lateinit var categoryInput: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        taskInput = findViewById(R.id.etNewTask)
        categoryInput = findViewById(R.id.etTaskCategory)
        val addTaskButton = findViewById<Button>(R.id.btnAddTask)
        val recyclerView = findViewById<RecyclerView>(R.id.rvTasks)
        val taskDateInput = findViewById<EditText>(R.id.etTaskDate)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
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
        adapter = TaskAdapter(recyclerView, mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
          //  val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            //lifecycleScope.launch(Dispatchers.IO) {
              //  val tasksForSelectedDate = taskDao.getTasksByDate2(selectedDate)
                //withContext(Dispatchers.Main) {
                  //  adapter.setTasks(tasksForSelectedDate)
                //}
            //}
        //}

        // Load all tasks sorted by category
        loadTasks()

        taskDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format selected date as "YYYY-MM-DD"
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                taskDateInput.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        // Handle task addition
        findViewById<Button>(R.id.btnAddTask).setOnClickListener {
            val taskTitle = taskInput.text.toString().trim()
            val taskCategory = findViewById<EditText>(R.id.etTaskCategory).text.toString().trim()

            if (taskTitle.isNotBlank()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val category = if (taskCategory.isNotBlank()) taskCategory else "General"
                val newTask = Task(title = taskTitle, isCompleted = false, category = category, date = currentDate)

                lifecycleScope.launch(Dispatchers.IO) {
                    taskDao.insertTask(newTask)
                    val updatedTasks = taskDao.getAllTasks().value // Fetch updated list

                    withContext(Dispatchers.Main) {
                        if (updatedTasks != null) {
                            adapter.setTasks(updatedTasks) // Update UI with new task list
                        }
                        taskInput.text.clear()
                        findViewById<EditText>(R.id.etTaskCategory).text.clear()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }


        findViewById<Button>(R.id.btnOpenCalendar).setOnClickListener {
            // Open the Calendar Activity
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        // Handle category selection for filtering
        setupCategoryFilter()
    }

    private fun loadTasks(selectedCategory: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = if (selectedCategory == null || selectedCategory == "All") {
                taskDao.getTasksSortedByCategory()
            } else {
                taskDao.getTasksByCategorySorted(selectedCategory)
            }

            withContext(Dispatchers.Main) {
                adapter.setTasks(tasks)
            }
        }
    }

    private fun setupCategoryFilter() {
        CoroutineScope(Dispatchers.IO).launch {
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
                        loadTasks()
                    }
                }
            }
        }
    }
}
