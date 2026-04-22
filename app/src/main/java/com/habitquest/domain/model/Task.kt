package com.habitquest.domain.model

data class Task(
    val id: Long = 0,
    val name: String,
    val category: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
