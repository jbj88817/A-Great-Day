package us.bojie.a_great_day.ui.time_task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.databinding.ItemTaskBinding

class TasksAdapter(
    private val listener: OnItemClickListener,
    private val viewModel: TimeTaskViewModel
) :
    ListAdapter<Task, TasksAdapter.TasksViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TasksViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckBoxClick(task, checkBoxCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewEstimate.text = task.estimate
                textViewName.paint.isStrikeThruText = task.completed
            }
        }

        fun onReorder(from: Int?, to: Int?) {
            if (from == null || to == null) return
            if (from > to) {
                val draggedItem = getItem(from)
                viewModel.updateTask(draggedItem.copy(order = to), false)
                for (i in to..from) {
                    val item = getItem(i)
                    viewModel.updateTask(item.copy(order = i + 1), false)
                }
                viewModel.updateTask(draggedItem.copy(order = to))
            } else if (from < to) {
                val draggedItem = getItem(from)
                viewModel.updateTask(draggedItem.copy(order = to), false)
                for (i in from..to) {
                    val item = getItem(i)
                    viewModel.updateTask(item.copy(order = i - 1), false)
                }
                viewModel.updateTask(draggedItem.copy(order = to))
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}