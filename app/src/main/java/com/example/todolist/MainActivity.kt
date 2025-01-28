package com.example.todolist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.TaskDao
import com.example.todolist.model.Task
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    // Declare database, DAO, and adapter variables
    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for this activity
        setContentView(R.layout.activity_main)

        // Find the EditText view for entering new tasks
        val taskInput = findViewById<EditText>(R.id.etNewTask)

        // Initialize the Room database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "tasks-db" // Database name
        ).build()

        // Get the TaskDao from the database
        taskDao = db.taskDao()

        // Set up the RecyclerView to display the list of tasks
        val recyclerView: RecyclerView = findViewById(R.id.rvTasks)
        adapter = TaskAdapter(recyclerView, mutableListOf()) // Initialize the adapter with an empty list
        recyclerView.adapter = adapter // Set the adapter to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this) // Use a LinearLayoutManager for the RecyclerView

        // Load tasks from the database and observe changes
        lifecycleScope.launch {
            taskDao.getAllTasks().observe(this@MainActivity) { tasks ->
                // Update the adapter's task list when the data changes
                adapter.setTasks(tasks)
            }
        }

        // Set up the "Add Task" button click listener
        findViewById<Button>(R.id.btnAddTask).setOnClickListener {
            // Get the task title from the input field
            val taskTitle = taskInput.text.toString()

            // Check if the input is not blank
            if (taskTitle.isNotBlank()) {
                // Create a new Task object
                val newTask = Task(title = taskTitle, isCompleted = false)

                // Add the new task to the adapter's list
                adapter.addTask(newTask)

                // Clear the input field after adding the task
                taskInput.text.clear()

                // Insert the new task into the database
                lifecycleScope.launch {
                    taskDao.insertTask(newTask)
                }
            } else {
                // Show a toast message if the input is blank
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }
    }
}