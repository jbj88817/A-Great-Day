package us.bojie.a_great_day.ui.time_task

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        }

        viewModel.startTimer()
        viewModel.countDownLiveData.observe(viewLifecycleOwner) {
            binding.tvTime.text = it
        }

//        viewModel.addFireBaseTestData()
        viewModel.refreshFirebaseData()

        viewModel.todayTasksLiveData.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

    }

    override fun onItemClick(task: Task) {
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
    }
}