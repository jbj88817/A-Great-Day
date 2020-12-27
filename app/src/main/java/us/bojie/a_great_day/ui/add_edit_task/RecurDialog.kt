package us.bojie.a_great_day.ui.add_edit_task

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import us.bojie.a_great_day.R
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.data.firebase.FirebaseManager
import us.bojie.a_great_day.databinding.DialogSetRecurBinding
import javax.inject.Inject

@AndroidEntryPoint
class RecurDialog : DialogFragment(R.layout.dialog_set_recur) {
    private lateinit var binding: DialogSetRecurBinding

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSetRecurBinding.bind(view)

        val task = arguments?.getParcelable<Task>("task") ?: return

        // Init radio button state
        if (task.repeat == null) {
            binding.btnNoRepeat.isChecked = true
        } else if (task.repeat == 1) {
            binding.btnDaily.isChecked = true
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnDone.setOnClickListener {
            if (binding.btnDaily.isChecked) {
                lifecycleScope.launchWhenCreated {
                    firebaseManager.updateTask(task.copy(repeat = 1))
                }
            } else {
                lifecycleScope.launchWhenCreated {
                    firebaseManager.updateTask(task.copy(repeat = null))
                }
            }
            dismiss()
        }
    }
}