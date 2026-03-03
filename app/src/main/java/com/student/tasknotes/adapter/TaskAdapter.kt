package com.student.tasknotes.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.tasknotes.R
import com.student.tasknotes.databinding.ItemTaskBinding
import com.student.tasknotes.model.Task

class TaskAdapter(
    private val onDeleteClick:    (Task) -> Unit,
    private val onCompleteToggle: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Internal task list
    private var taskList: MutableList<Task> = mutableListOf()


    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {

            // Set title text
            binding.tvTaskTitle.text = task.title

            // Show/hide description based on content
            if (task.description.isNotEmpty()) {
                binding.tvTaskDescription.text        = task.description
                binding.tvTaskDescription.visibility  = View.VISIBLE
            } else {
                binding.tvTaskDescription.visibility  = View.GONE
            }

            binding.checkBoxComplete.setOnCheckedChangeListener(null)
            binding.checkBoxComplete.isChecked = task.isCompleted

            // Apply visual style based on completion
            applyCompletedStyle(task.isCompleted)

            // Now safely re-attach the listener
            binding.checkBoxComplete.setOnCheckedChangeListener { _, _ ->
                onCompleteToggle(task)
            }

            // Delete button
            binding.btnDelete.setOnClickListener {
                onDeleteClick(task)
            }
        }

        private fun applyCompletedStyle(isCompleted: Boolean) {
            if (isCompleted) {
                binding.tvTaskTitle.paintFlags =
                    binding.tvTaskTitle.paintFlags or
                            Paint.STRIKE_THRU_TEXT_FLAG

                // Grey color for completed tasks
                binding.tvTaskTitle.setTextColor(
                    binding.root.context.getColor(R.color.colorCompleted)
                )
            } else {
                binding.tvTaskTitle.paintFlags =
                    binding.tvTaskTitle.paintFlags and
                            Paint.STRIKE_THRU_TEXT_FLAG.inv()

                // Restore normal text color
                binding.tvTaskTitle.setTextColor(
                    binding.root.context.getColor(R.color.colorText)
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int = taskList.size

    fun submitList(newList: List<Task>) {
        taskList.clear()
        taskList.addAll(newList)
        notifyDataSetChanged()
    }
}