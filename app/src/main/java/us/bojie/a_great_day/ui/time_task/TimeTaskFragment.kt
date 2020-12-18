package us.bojie.a_great_day.ui.time_task

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import us.bojie.a_great_day.R
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.databinding.FragmentTimeTaskBinding

@AndroidEntryPoint
class TimeTaskFragment : Fragment(R.layout.fragment_time_task), TasksAdapter.OnItemClickListener {

    private val viewModel: TimeTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding: FragmentTimeTaskBinding = FragmentTimeTaskBinding.bind(view)
        val taskAdapter = TasksAdapter(this)

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            fabAddTask.setOnClickListener {
                findNavController().navigate(R.id.action_timeTaskFragment_to_addEditTaskFragment)
            }

            fabDeleteAllTask.setOnClickListener {
                taskAdapter.currentList.filter { it.completed }.map {
                    viewModel.deleteTask(it)
                }
            }
        }

        viewModel.init()
        viewModel.countDownLiveData.observe(viewLifecycleOwner) {
            binding.tvTime.text = it
        }

        viewModel.todayTasksLiveData.observe(viewLifecycleOwner) { tasks ->
            setupTotalHours(tasks, binding)
            taskAdapter.submitList(tasks)
        }
    }

    private fun setupTotalHours(
        tasks: List<Task>,
        binding: FragmentTimeTaskBinding
    ) {
        var totalHours = 0f
        tasks.map {
            val estimateStr = it.estimate
            totalHours += estimateStr.substring(0, estimateStr.indexOf("h")).toFloat()
        }
        if (totalHours.toString() != "0.0") {
            binding.textViewTotalHour.text =
                getString(R.string.total_hour, totalHours.toString())
        }
    }

    override fun onItemClick(task: Task) {
        findNavController().navigate(
            R.id.action_timeTaskFragment_to_addEditTaskFragment, bundleOf(
                "task" to task
            )
        )
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.updateTask(task.copy(completed = isChecked))
    }
}