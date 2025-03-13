package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks1(): List<Task>

    @Insert
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM tasks WHERE category = :category")
    fun getTasksByCategory(category: String): LiveData<List<Task>> // Changed to LiveData

    @Query("SELECT DISTINCT category FROM tasks")
    suspend fun getAllCategories(): List<String> // No change, used in background

    @Query("SELECT * FROM tasks ORDER BY category ASC")
    fun getTasksSortedByCategory(): LiveData<List<Task>> // Changed to LiveData

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY title ASC")
    fun getTasksByCategorySorted(category: String): LiveData<List<Task>> // Changed to LiveData

    @Query("SELECT * FROM tasks WHERE date = :selectedDate")
    fun getTasksByDate(selectedDate: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE date = :selectedDate")
    suspend fun getTasksByDate2(selectedDate: String): List<Task>

    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getTasksByDate1(date: String): LiveData<List<Task>> // No change, already LiveData
}