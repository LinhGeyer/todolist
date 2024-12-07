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
import com.example.todolist.model.Task


class TaskAdapter(private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTaskCompleted: CheckBox = itemView.findViewById(R.id.cbTaskCompleted)
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvTaskName.text = task.name
        holder.cbTaskCompleted.isChecked = task.isCompleted

        // Update UI for completed task
        holder.tvTaskName.paintFlags = if (task.isCompleted) {
            holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Handle checkbox toggle
        holder.cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            notifyItemChanged(position)
        }

        // Handle long-press to delete task
        holder.cbTaskCompleted.setOnLongClickListener {
            if (!holder.itemView.isLayoutRequested) {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes") { _, _ ->
                        tasks.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
            true
        }

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun addTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }
}
