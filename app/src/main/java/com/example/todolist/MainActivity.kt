package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.model.Task


class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNewTask: EditText = findViewById(R.id.etNewTask)
        val btnAddTask: Button = findViewById(R.id.btnAddTask)
        val rvTasks: RecyclerView = findViewById(R.id.rvTasks)

        val recyclerView: RecyclerView = findViewById(R.id.rvTasks)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Disable prefetching
        layoutManager.isItemPrefetchEnabled = false

        // Set adapter
        val adapter = TaskAdapter(mutableListOf())
        recyclerView.adapter = adapter
        taskAdapter = TaskAdapter(mutableListOf())

        rvTasks.adapter = taskAdapter
        rvTasks.layoutManager = LinearLayoutManager(this)

        btnAddTask.setOnClickListener {
            val taskName = etNewTask.text.toString()
            if (taskName.isNotEmpty()) {
                val task = Task(taskName)
                taskAdapter.addTask(task)
                etNewTask.text.clear()
            }
        }
    }
}
