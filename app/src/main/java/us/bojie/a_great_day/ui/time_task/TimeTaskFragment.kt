package us.bojie.a_great_day.ui.time_task

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import us.bojie.a_great_day.R
import us.bojie.a_great_day.databinding.FragmentTimeTaskBinding

@AndroidEntryPoint
class TimeTaskFragment : Fragment(R.layout.fragment_time_task) {

    private val viewModel: TimeTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding: FragmentTimeTaskBinding = FragmentTimeTaskBinding.bind(view)

        viewModel.startTimer()
        viewModel.countDownLiveData.observe(viewLifecycleOwner) {
            binding.tvTime.text = it
        }
    }
}