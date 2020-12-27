package us.bojie.a_great_day.ui.time_task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import us.bojie.a_great_day.app_widget.CountDown
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.data.firebase.FirebaseManager
import us.bojie.a_great_day.util.Util

class TimeTaskViewModel @ViewModelInject constructor(
    private val firebaseManager: FirebaseManager
) : ViewModel() {

    private val _countDownLiveData = MutableLiveData<String>()
    val countDownLiveData: LiveData<String> = _countDownLiveData

    private val _todayTasksLiveData = MutableLiveData<List<Task>>()
    val todayTasksLiveData: LiveData<List<Task>> = _todayTasksLiveData

    fun init() {
        startTimer()

        viewModelScope.launch {
            _todayTasksLiveData.value = firebaseManager.getTodayTasks()
        }
    }

    fun refreshTask() {
        viewModelScope.launch {
            _todayTasksLiveData.value = firebaseManager.getTodayTasks()
        }
    }

    fun updateTask(task: Task, needRefresh: Boolean = true) {
        viewModelScope.launch {
            firebaseManager.updateTask(task)
            if (needRefresh) {
                _todayTasksLiveData.value = firebaseManager.getTodayTasks()
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            firebaseManager.deleteTask(task)
            _todayTasksLiveData.value = firebaseManager.getTodayTasks()
        }
    }

    fun createNextDayRepeatTask(task: Task) = viewModelScope.launch {
        firebaseManager.updateTaskToNextDay(task, false)
    }

    private fun startTimer() {
        val millisToGo = Util.getEndOfDayInMillis()

        CountDown(millisToGo, 1000) { text ->
            _countDownLiveData.value = text
        }.start()
    }

    companion object {
        const val TAG = "TimeTaskViewModel"
    }
}

