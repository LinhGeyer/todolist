package com.example.todolist.adapter

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.graphics.Paint
import androidx.compose.foundation.layout.size
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Task

// Adapter class for managing the list of tasks in a RecyclerView
class TaskAdapter(private val recyclerView: RecyclerView, private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Handler to ensure UI updates are performed on the main thread
    private val mainHandler = Handler(Looper.getMainLooper())

    // ViewHolder class to hold references to the views for each task item
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTaskCompleted: CheckBox = itemView.findViewById(R.id.cbTaskCompleted) // CheckBox for task completion
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName) // TextView for task title
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

        // Update the TextView's paint flags to show strike-through text for completed tasks
        holder.tvTaskName.paintFlags = if (task.isCompleted) {
            holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Handle CheckBox toggle to update task completion status
        holder.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            recyclerView.post {
                notifyItemChanged(position) // Notify adapter of data change
            }
        }

        // Handle long-press on the CheckBox to delete the task
        holder.cbTaskCompleted.setOnLongClickListener {
            // Show a confirmation dialog for task deletion
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes") { _, _ ->
                    tasks.removeAt(holder.adapterPosition) // Remove the task from the list
                    recyclerView.post {
                        notifyItemRemoved(holder.adapterPosition) // Notify adapter of item removal
                    }
                }
                .setNegativeButton("No", null) // Do nothing if "No" is clicked
                .show()
            true // Return true to indicate the long-press event is consumed
        }
    }

    // Returns the total number of tasks in the list
    override fun getItemCount(): Int {
        return tasks.size
    }

    // Adds a new task to the list and notifies the adapter
    fun addTask(task: Task) {
        tasks.add(task)
        mainHandler.post {
            recyclerView.post {
                notifyItemInserted(tasks.size - 1) // Notify adapter of item insertion
            }
        }
    }

    // Updates the list of tasks and notifies the adapter of the change
    fun setTasks(newTasks: List<Task>?) {
        if (newTasks != null) {
            tasks.clear() // Clear the current list
            tasks.addAll(newTasks) // Add all new tasks
            recyclerView.post {
                notifyDataSetChanged() // Notify adapter of data set change
            }
        }
    }
}