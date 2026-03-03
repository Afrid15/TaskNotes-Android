package com.student.tasknotes.model

data class Task(

    val id: Long = System.currentTimeMillis(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)