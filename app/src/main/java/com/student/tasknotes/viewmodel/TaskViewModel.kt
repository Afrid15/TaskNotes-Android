package com.student.tasknotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.student.tasknotes.TaskRepository
import com.student.tasknotes.model.Task

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks
    var draftTitle: String       = ""
    var draftDescription: String = ""

    init {
        refreshTasks()
    }

    fun refreshTasks() {
        _tasks.value = repository.loadTasks()
    }

    fun addTask(title: String, description: String): Boolean {

        // Trim whitespace from both ends
        val cleanTitle       = title.trim()
        val cleanDescription = description.trim()

        // Reject empty or blank titles
        if (cleanTitle.isBlank()) return false

        // Enforce maximum title length
        if (cleanTitle.length > 100) return false

        // Create task with clean validated data only
        val newTask = Task(
            title       = cleanTitle,
            description = cleanDescription
        )

        // Save to SharedPreferences
        repository.addTask(newTask)

        // Refresh LiveData so observers update immediately
        refreshTasks()

        // Clear draft text after successful save
        draftTitle       = ""
        draftDescription = ""

        return true
    }

    fun deleteTask(taskId: Long) {
        repository.deleteTask(taskId)
        refreshTasks()
    }

    fun toggleTaskComplete(taskId: Long) {
        repository.toggleTaskComplete(taskId)
        refreshTasks()
    }

    fun getTaskCount(): Int {
        return _tasks.value?.size ?: 0
    }
}