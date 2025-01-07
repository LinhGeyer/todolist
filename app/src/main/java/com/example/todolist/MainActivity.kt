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
    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val taskInput = findViewById<EditText>(R.id.etNewTask)


        // Initialize Room database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "tasks-db"
        ).build()
        taskDao = db.taskDao()

        // Observe tasks from the database
        val recyclerView: RecyclerView = findViewById(R.id.rvTasks)
        adapter = TaskAdapter(recyclerView, mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load tasks from database
        lifecycleScope.launch {
            taskDao.getAllTasks().observe(this@MainActivity) { tasks ->
                adapter.setTasks(tasks)
            }
        }

        // Add a new task
        findViewById<Button>(R.id.btnAddTask).setOnClickListener {

            val taskTitle = taskInput.text.toString()
            if (taskTitle.isNotBlank()) {
                val newTask = Task(title = taskTitle, isCompleted = false)
                adapter.addTask(newTask)
                taskInput.text.clear() // Clear the input field after adding
                lifecycleScope.launch {
                    taskDao.insertTask(newTask)

                }
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }

        }
    }
}