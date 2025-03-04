package com.example.todolist

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)  // Create this XML layout

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        // Handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(this, "Selected: $selectedDate", Toast.LENGTH_SHORT).show()
            // TODO: Display tasks for the selected date
        }
    }
}
