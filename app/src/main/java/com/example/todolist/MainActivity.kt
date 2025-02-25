package com.example.todolist

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


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter
    private lateinit var spinnerCategory: Spinner
    private lateinit var taskInput: EditText
    private lateinit var categoryInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        taskInput = findViewById(R.id.etNewTask)
        categoryInput = findViewById(R.id.etTaskCategory)
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
        adapter = TaskAdapter(recyclerView, mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load all tasks sorted by category
        loadTasks()

        // Handle task addition
        addTaskButton.setOnClickListener {
            val taskTitle = taskInput.text.toString().trim()
            val category = categoryInput.text.toString().trim().ifEmpty { "General" }

            if (taskTitle.isNotEmpty()) {
                val newTask = Task(title = taskTitle, isCompleted = false, category = category)
                CoroutineScope(Dispatchers.IO).launch {
                    taskDao.insertTask(newTask)
                    loadTasks() // Reload tasks after insertion
                    withContext(Dispatchers.Main) {
                        taskInput.text.clear()
                        categoryInput.text.clear()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
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
