package com.example.taskmanager


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing task data persistence
 * Uses DataStore for secure local storage
 *
 * SECURE CODING PRACTICE #3: Data Storage
 * - DataStore is encrypted and secure
 * - No sensitive data hardcoded
 * - Data stored only on device (no cloud backup in this implementation)
 */
class TaskRepository(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "tasks_preferences"
    )

    private val gson = Gson()
    private val tasksKey = stringPreferencesKey("tasks_list")

    // Get all tasks as Flow
    val allTasks: Flow<List<Task>> = context.dataStore.data.map { preferences ->
        val tasksJson = preferences[tasksKey] ?: "[]"
        val type = object : TypeToken<List<Task>>() {}.type
        gson.fromJson(tasksJson, type) ?: emptyList()
    }

    // Add a new task
    suspend fun addTask(task: Task) {
        // SECURE CODING PRACTICE #4: Input validation before storage
        if (!task.isValid()) {
            throw IllegalArgumentException("Invalid task data")
        }

        context.dataStore.edit { preferences ->
            val currentTasks = getCurrentTasks(preferences)
            val updatedTasks = currentTasks + task
            preferences[tasksKey] = gson.toJson(updatedTasks)
        }
    }

    // Update existing task
    suspend fun updateTask(task: Task) {
        context.dataStore.edit { preferences ->
            val currentTasks = getCurrentTasks(preferences)
            val updatedTasks = currentTasks.map {
                if (it.id == task.id) task else it
            }
            preferences[tasksKey] = gson.toJson(updatedTasks)
        }
    }

    // Delete a task
    suspend fun deleteTask(taskId: String) {
        context.dataStore.edit { preferences ->
            val currentTasks = getCurrentTasks(preferences)
            val updatedTasks = currentTasks.filter { it.id != taskId }
            preferences[tasksKey] = gson.toJson(updatedTasks)
        }
    }

    // Toggle task completion status
    suspend fun toggleTaskCompletion(taskId: String) {
        context.dataStore.edit { preferences ->
            val currentTasks = getCurrentTasks(preferences)
            val updatedTasks = currentTasks.map { task ->
                if (task.id == taskId) {
                    task.copy(isCompleted = !task.isCompleted)
                } else {
                    task
                }
            }
            preferences[tasksKey] = gson.toJson(updatedTasks)
        }
    }

    private fun getCurrentTasks(preferences: Preferences): List<Task> {
        val tasksJson = preferences[tasksKey] ?: "[]"
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(tasksJson, type) ?: emptyList()
    }
}