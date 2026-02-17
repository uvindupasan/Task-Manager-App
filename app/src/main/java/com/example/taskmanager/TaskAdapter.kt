package com.example.taskmanager


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onCheckClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxCompleted)
        private val titleText: TextView = itemView.findViewById(R.id.textTitle)
        private val descriptionText: TextView = itemView.findViewById(R.id.textDescription)
        private val timeText: TextView = itemView.findViewById(R.id.textTime)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(task: Task) {
            titleText.text = task.title
            descriptionText.text = task.description
            checkBox.isChecked = task.isCompleted

            // Format timestamp
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            timeText.text = dateFormat.format(Date(task.timestamp))

            // Apply strikethrough for completed tasks
            if (task.isCompleted) {
                titleText.paintFlags = titleText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                descriptionText.paintFlags = descriptionText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                titleText.paintFlags = titleText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                descriptionText.paintFlags = descriptionText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Click listeners
            itemView.setOnClickListener { onTaskClick(task) }
            deleteButton.setOnClickListener { onDeleteClick(task) }
            checkBox.setOnClickListener { onCheckClick(task) }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}