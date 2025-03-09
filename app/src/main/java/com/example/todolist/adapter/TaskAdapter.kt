package com.example.todolist.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.TaskDao
import com.example.todolist.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAdapter(
    private val taskDao: TaskDao, // Pass TaskDao directly
    private val tasks: MutableList<Task> = mutableListOf() // Initialize with an empty list
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder class to hold references to the views for each task item
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTaskCompleted: CheckBox = itemView.findViewById(R.id.cbTaskCompleted) // CheckBox for task completion
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName) // TextView for task title
        val tvTaskCategory: TextView = itemView.findViewById(R.id.tvTaskCategory) // TextView for task category
    }

    // Inflates the task item layout and creates a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    // Binds the task data to the views in the ViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Set the task title in the TextView
        holder.tvTaskName.text = task.title

        // Set the CheckBox state based on task completion
        holder.cbTaskCompleted.isChecked = task.isCompleted

        // Display category
        holder.tvTaskCategory.text = task.category

        // Update the TextView's paint flags to show strike-through text for completed tasks
        updateStrikeThroughText(holder.tvTaskName, task.isCompleted)

        // Handle CheckBox toggle to update task completion status
        holder.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            updateStrikeThroughText(holder.tvTaskName, isChecked)
            updateTaskCompletion(task)
        }

        // Handle long-press on the CheckBox to delete the task
        holder.cbTaskCompleted.setOnLongClickListener {
            showDeleteTaskDialog(holder.itemView.context, task, position)
            true
        }
    }

    // Returns the total number of tasks in the list
    override fun getItemCount(): Int {
        return tasks.size
    }

    // Adds a new task to the list and notifies the adapter
    fun addTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1) // Notify adapter of item insertion
    }

    // Updates the task list and notifies the adapter
    fun setTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged() // Notify adapter of data change
    }

    // Helper method to update strike-through text
    private fun updateStrikeThroughText(textView: TextView, isCompleted: Boolean) {
        textView.paintFlags = if (isCompleted) {
            textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    // Helper method to update task completion status in the database
    private fun updateTaskCompletion(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateTask(task)
        }
    }

    // Helper method to show a delete task dialog
    private fun showDeleteTaskDialog(context: android.content.Context, task: Task, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    taskDao.deleteTask(task)
                    withContext(Dispatchers.Main) {
                        tasks.removeAt(position)
                        notifyItemRemoved(position) // Notify adapter of item removal
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}