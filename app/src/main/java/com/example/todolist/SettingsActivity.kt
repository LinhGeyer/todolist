package com.example.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.todolist.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the current theme preference
        val sharedPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val currentTheme = sharedPrefs.getString("theme", "system") ?: "system"

        // Set the radio button based on the current theme
        when (currentTheme) {
            "light" -> binding.radioLight.isChecked = true
            "dark" -> binding.radioDark.isChecked = true
            else -> binding.radioSystem.isChecked = true
        }

        // Handle theme selection
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioLight -> setTheme("light")
                R.id.radioDark -> setTheme("dark")
                R.id.radioSystem -> setTheme("system")
            }
        }
    }

    private fun setTheme(theme: String) {
        val sharedPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("theme", theme)
            apply()
        }

        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        // Restart the activity to apply the new theme
        recreate()
    }
}