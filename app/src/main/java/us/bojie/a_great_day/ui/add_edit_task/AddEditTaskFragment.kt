package us.bojie.a_great_day.ui.add_edit_task

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import us.bojie.a_great_day.R
import us.bojie.a_great_day.databinding.FragmentAddEditTaskBinding
import us.bojie.a_great_day.util.exhaustive

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            editTextTaskName.setText(viewModel.taskName)
            editTextTaskName.requestFocus()
            viewModel.oldTaskName =
                if (viewModel.taskName.isNotBlank()) viewModel.taskName else null
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text =
                getString(R.string.created, viewModel.task?.createdDateFormatted)

            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            numberPicker.apply {
                val disArray =
                    arrayOf(
                        "0.5h", "1h", "1.5h", "2h", "2.5h", "3h", "3.5h", "4h", "4.5h",
                        "5h", "5.5h", "6h", "6.5h", "7h", "7.5h", "8h"
                    )
                displayedValues = disArray
                minValue = 0
                maxValue = 15
                value = if (viewModel.taskEstimate.isNotBlank()) {
                    disArray.indexOf(viewModel.taskEstimate)
                } else {
                    viewModel.taskEstimate = disArray[0]
                    0
                }
                setOnValueChangedListener { _, _, newVal ->
                    viewModel.taskEstimate = disArray[newVal]
                }
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }

            fabDeleteTask.setOnClickListener {
                viewModel.onDeleteClick()
            }

            fabNextDay.apply {
                visibility = if (viewModel.taskName.isNotBlank())
                    View.VISIBLE
                else View.GONE
                setOnClickListener {
                    viewModel.onSkipToNextDayClick()
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTaskName.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }
}