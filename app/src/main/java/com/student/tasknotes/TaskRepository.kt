package com.student.tasknotes

import android.content.Context
import android.content.SharedPreferences
import com.student.tasknotes.model.Task
import org.json.JSONArray
import org.json.JSONObject

class TaskRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME  = "tasknotes_prefs"
        private const val KEY_TASKS   = "tasks_data"

        // JSON field name constants
        private const val FIELD_ID          = "id"
        private const val FIELD_TITLE       = "title"
        private const val FIELD_DESCRIPTION = "description"
        private const val FIELD_COMPLETED   = "isCompleted"
        private const val FIELD_CREATED_AT  = "createdAt"
    }

    fun saveTasks(tasks: List<Task>) {
        val jsonArray = JSONArray()

        for (task in tasks) {
            val jsonObject = JSONObject().apply {
                put(FIELD_ID,          task.id)
                put(FIELD_TITLE,       task.title)
                put(FIELD_DESCRIPTION, task.description)
                put(FIELD_COMPLETED,   task.isCompleted)
                put(FIELD_CREATED_AT,  task.createdAt)
            }
            jsonArray.put(jsonObject)
        }


        prefs.edit()
            .putString(KEY_TASKS, jsonArray.toString())
            .apply()
    }

    fun loadTasks(): MutableList<Task> {
        val taskList  = mutableListOf<Task>()
        val jsonString = prefs.getString(KEY_TASKS, "") ?: ""

        // Nothing saved yet — return empty list
        if (jsonString.isEmpty()) return taskList

        try {
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                taskList.add(
                    Task(
                        id          = obj.getLong(FIELD_ID),
                        title       = obj.getString(FIELD_TITLE),
                        description = obj.getString(FIELD_DESCRIPTION),
                        isCompleted = obj.getBoolean(FIELD_COMPLETED),
                        createdAt   = obj.getLong(FIELD_CREATED_AT)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return taskList
    }

    fun addTask(task: Task) {
        val tasks = loadTasks()
        tasks.add(task)
        saveTasks(tasks)
    }

    fun deleteTask(taskId: Long) {
        val tasks = loadTasks()
        tasks.removeAll { it.id == taskId }
        saveTasks(tasks)
    }

    fun toggleTaskComplete(taskId: Long) {
        val tasks = loadTasks()
        val index = tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            tasks[index] = tasks[index].copy(
                isCompleted = !tasks[index].isCompleted
            )
            saveTasks(tasks)
        }
    }
}