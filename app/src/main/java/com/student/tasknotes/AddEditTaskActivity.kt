package com.student.tasknotes

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.student.tasknotes.databinding.ActivityAddEditTaskBinding
import com.student.tasknotes.viewmodel.TaskViewModel

class AddEditTaskActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityAddEditTaskBinding

    // Shared ViewModel
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        restoreDraftText()

        // Save button
        binding.btnSaveTask.setOnClickListener {
            saveTask()
        }

        // Cancel button
        binding.btnCancel.setOnClickListener {
            taskViewModel.draftTitle = ""
            taskViewModel.draftDescription = ""
            finish()
        }
    }

    private fun restoreDraftText() {
        if (taskViewModel.draftTitle.isNotEmpty()) {
            binding.etTaskTitle.setText(taskViewModel.draftTitle)
        }
        if (taskViewModel.draftDescription.isNotEmpty()) {
            binding.etTaskDescription.setText(taskViewModel.draftDescription)
        }
    }

    override fun onPause() {
        super.onPause()
        // Save draft text to ViewModel before screen closes
        taskViewModel.draftTitle =
            binding.etTaskTitle.text.toString()
        taskViewModel.draftDescription =
            binding.etTaskDescription.text.toString()
    }

    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString()
        val description = binding.etTaskDescription.text.toString()

        // Check if title is empty
        if (title.trim().isBlank()) {
            binding.tilTaskTitle.error = getString(R.string.msg_enter_title)
            return
        }

        // Check title length
        if (title.trim().length > 100) {
            binding.tilTaskTitle.error = getString(R.string.msg_title_too_long)
            return
        }

        // Clear any previous error
        binding.tilTaskTitle.error = null

        // Save via ViewModel (returns true if successful)
        val saved = taskViewModel.addTask(title, description)

        if (saved) {
            showToast(getString(R.string.msg_task_saved))
            finish() // Go back to MainActivity
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(
            this, message, android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}