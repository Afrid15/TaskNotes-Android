package com.student.tasknotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.tasknotes.adapter.TaskAdapter
import com.student.tasknotes.databinding.ActivityMainBinding
import com.student.tasknotes.viewmodel.TaskViewModel


class MainActivity : AppCompatActivity() {

    // ViewBinding — safe access to UI views
    private lateinit var binding: ActivityMainBinding

    // ViewModel — survives screen rotation
    private val taskViewModel: TaskViewModel by viewModels()

    // RecyclerView adapter
    private lateinit var taskAdapter: TaskAdapter
    private val addTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView with adapter
        setupRecyclerView()

        // Observe LiveData from ViewModel
        observeViewModel()

        // FAB → open Add Task screen
        binding.fabAddTask.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        taskViewModel.refreshTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onDeleteClick = { task ->
                taskViewModel.deleteTask(task.id)
                showToast(getString(R.string.msg_task_deleted))
            },
            onCompleteToggle = { task ->
                taskViewModel.toggleTaskComplete(task.id)
            }
        )

        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun observeViewModel() {
        taskViewModel.tasks.observe(this) { taskList ->
            // Update adapter with new data
            taskAdapter.submitList(taskList)

            // Show or hide empty state message
            updateEmptyState(taskList.isEmpty())
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvEmptyState.visibility     = View.VISIBLE
            binding.recyclerViewTasks.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility     = View.GONE
            binding.recyclerViewTasks.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(
            this,
            message,
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}