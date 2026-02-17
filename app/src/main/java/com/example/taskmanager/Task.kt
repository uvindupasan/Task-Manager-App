package com.example.taskmanager


import java.util.UUID

/**
 * Data model representing a task/note
 *
 * SECURE CODING PRACTICE #1: Data Validation
 * - UUID ensures unique, non-guessable task IDs
 * - Prevents ID collision and manipulation attacks
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * SECURE CODING PRACTICE #2: Input Validation
     * Validates task data before creation
     */
    fun isValid(): Boolean {
        return title.isNotBlank() &&
                title.length <= 100 &&
                description.length <= 500
    }
}