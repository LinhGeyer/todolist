package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Insert
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM tasks WHERE category = :category")
    suspend fun getTasksByCategory(category: String): List<Task>

    @Query("SELECT DISTINCT category FROM tasks")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT * FROM tasks ORDER BY category ASC")
    fun getTasksSortedByCategory(): List<Task>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY title ASC")
    fun getTasksByCategorySorted(category: String): List<Task>

    @Query("SELECT * FROM tasks WHERE date = :selectedDate")
    fun getTasksByDate(selectedDate: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE date = :selectedDate")
    fun getTasksByDate2(selectedDate: String): List<Task>

    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getTasksByDate1(date: String): LiveData<List<Task>>

}