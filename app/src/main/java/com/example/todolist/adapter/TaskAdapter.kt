package com.example.todolist.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.TaskDao
import com.example.todolist.model.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskAdapter(
    private val taskDao: TaskDao,
    private val tasks: MutableList<Task> = mutableListOf() // Default empty list
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTaskCompleted: CheckBox = itemView.findViewById(R.id.cbTaskCompleted)
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val tvTaskCategory: TextView = itemView.findViewById(R.id.tvTaskCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Set task title and category
        holder.tvTaskName.text = task.title
        holder.tvTaskCategory.text = task.category

        // Set checkbox state
        holder.cbTaskCompleted.isChecked = task.isCompleted

        // Update strike-through text for completed tasks
        updateStrikeThroughText(holder.tvTaskName, task.isCompleted)

        // Handle checkbox toggle
        holder.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            updateStrikeThroughText(holder.tvTaskName, isChecked)

            // Update task completion status in the database
            CoroutineScope(Dispatchers.IO).launch {
                taskDao.updateTask(task)
            }

            // Show Snackbar when task is completed
            if (isChecked) {
                showCompletionSnackbar(holder.itemView, task.title)
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    private fun updateStrikeThroughText(textView: TextView, isCompleted: Boolean) {
        textView.paintFlags = if (isCompleted) {
            textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun showCompletionSnackbar(view: View, taskTitle: String) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        // Inflate the custom layout
        val customView = LayoutInflater.from(view.context).inflate(R.layout.custom_snackbar, null)

        // Set the task title
        val snackbarText = customView.findViewById<TextView>(R.id.snackbar_text)
        snackbarText.text = "You've completed the task: $taskTitle"

        // Set the dismiss action
        val snackbarAction = customView.findViewById<Button>(R.id.snackbar_action)
        snackbarAction.setOnClickListener {
            snackbar.dismiss()
        }

        // Remove the default Snackbar content and add the custom layout
        snackbarLayout.removeAllViews()
        snackbarLayout.addView(customView)

        // Show the Snackbar at the top of the screen
        val params = snackbarLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, 0, 0) // Adjust margins if needed
        snackbarLayout.layoutParams = params

        snackbar.show()
    }
    fun setTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}