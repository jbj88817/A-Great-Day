package us.bojie.a_great_day.ui.time_task

import android.content.SharedPreferences
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
import us.bojie.a_great_day.ui.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class TimeTaskFragment : Fragment(R.layout.fragment_time_task), TasksAdapter.OnItemClickListener {

    private val viewModel: TimeTaskViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPreferences

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
        var totalHoursLeft = 0f
        tasks.map {
            val estimateStr = it.estimate
            if (!it.completed) {
                totalHoursLeft += estimateStr.substring(0, estimateStr.indexOf("h")).toFloat()
            }
            totalHours += estimateStr.substring(0, estimateStr.indexOf("h")).toFloat()
        }
        val totalHoursText = if (totalHoursLeft.toString() != "0.0") {
            getString(R.string.total_hour, totalHoursLeft.toString(), totalHours.toString())
        } else {
            getString(R.string.all_tasks_completed)
        }
        binding.textViewTotalHour.text = totalHoursText
        pref.edit().putString(MainActivity.TOTAL_HOURS_TEXT, totalHoursText).apply()
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