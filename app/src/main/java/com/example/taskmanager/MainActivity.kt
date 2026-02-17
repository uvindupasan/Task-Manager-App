package com.example.taskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.observe
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupFab()
        observeTasks()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewTasks)
        emptyStateView = findViewById(R.id.textEmptyState)

        adapter = TaskAdapter(
            onTaskClick = { task -> showEditDialog(task) },
            onDeleteClick = { task -> showDeleteConfirmation(task) },
            onCheckClick = { task -> viewModel.toggleTaskCompletion(task.id) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        val fab: FloatingActionButton = findViewById(R.id.fabAddTask)
        fab.setOnClickListener { showAddDialog() }
    }

    private fun observeTasks() {
        viewModel.allTasks.observe(this) { tasks ->
            adapter.submitList(tasks)

            if (tasks.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyStateView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyStateView.visibility = View.GONE
            }
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_task, null)

        val titleInput = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.editTextDescription)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()

                if (title.isBlank()) {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (title.length > 100) {
                    Toast.makeText(this, "Title too long (max 100 chars)", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.addTask(title, description)
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(task: Task) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_task, null)

        val titleInput = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.editTextDescription)

        titleInput.setText(task.title)
        descriptionInput.setText(task.description)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()

                if (title.isBlank()) {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedTask = task.copy(
                    title = title,
                    description = description
                )

                viewModel.updateTask(updatedTask)
                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTask(task.id)
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
