package com.example.taskmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel for managing task data and UI state
 * Survives configuration changes (screen rotation)
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)

    // LiveData that observes task changes
    val allTasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                description = description.trim()
            )
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId)
        }
    }
}