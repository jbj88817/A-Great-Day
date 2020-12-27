package us.bojie.a_great_day.ui.time_task

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import us.bojie.a_great_day.R
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.databinding.FragmentTimeTaskBinding
import us.bojie.a_great_day.ui.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class TimeTaskFragment : Fragment(R.layout.fragment_time_task), TasksAdapter.OnItemClickListener {

    private val viewModel: TimeTaskViewModel by viewModels()
    private lateinit var taskAdapter: TasksAdapter
    private lateinit var binding: FragmentTimeTaskBinding

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTimeTaskBinding.bind(view)
        taskAdapter = TasksAdapter(this, viewModel)

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator = null
            }
            itemTouchHelper.attachToRecyclerView(recyclerViewTasks)

            fabAddTask.setOnClickListener {
                findNavController().navigate(
                    R.id.action_timeTaskFragment_to_addEditTaskFragment, bundleOf(
                        "taskListSize" to taskAdapter.currentList.size
                    )
                )
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
            taskAdapter.submitList(tasks.sortedBy { it.order })
        }
    }

    fun refreshTask() {
        viewModel.refreshTask()
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

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                UP or
                        DOWN or
                        START or
                        END, 0
            ) {

                var from: Int? = null
                var to: Int? = null
                var saveHolder: RecyclerView.ViewHolder? = null

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as TasksAdapter
                    val formPos = viewHolder.adapterPosition
                    to = target.adapterPosition
                    adapter.notifyItemMoved(formPos, to!!)
                    return true
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                        from = viewHolder?.adapterPosition ?: 0
                        saveHolder = viewHolder
                    } else if (actionState == ACTION_STATE_IDLE) {
                        (saveHolder as TasksAdapter.TasksViewHolder).onReorder(from, to)
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onItemClick(task: Task) {
        findNavController().navigate(
            R.id.action_timeTaskFragment_to_addEditTaskFragment, bundleOf(
                "task" to task,
                "taskListSize" to taskAdapter.currentList.size
            )
        )
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        if (isChecked) {
            val viewHolder =
                binding.recyclerViewTasks.findViewHolderForAdapterPosition(task.order ?: return)
            (viewHolder as TasksAdapter.TasksViewHolder).onReorder(
                task.order,
                taskAdapter.currentList.size - 1
            )
            viewModel.updateTask(
                task.copy(
                    completed = isChecked,
                    order = taskAdapter.currentList.size - 1
                )
            )
            Snackbar.make(
                requireView(),
                getString(R.string.task_marked_as_completed),
                Snackbar.LENGTH_SHORT
            ).show()

            if (task.repeat == 1) {
                viewModel.createNextDayRepeatTask(task)
                Snackbar.make(
                    requireView(),
                    getString(R.string.task_next_cycle_created),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            viewModel.updateTask(task.copy(completed = isChecked))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> signOut()
        }
        return true
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        pref.edit().putString(MainActivity.USER_UID, "-1").apply()
        (requireActivity() as MainActivity).signIn()
    }
}