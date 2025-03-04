package com.example.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.model.Task

@Database(entities = [Task::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}