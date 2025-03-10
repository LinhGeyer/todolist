package com.example.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    var isCompleted: Boolean,
    val category: String,
    val date: String, //DD/MM/YYYY
)