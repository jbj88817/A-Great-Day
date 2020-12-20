package us.bojie.a_great_day.ui.add_edit_task

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import us.bojie.a_great_day.data.Task
import us.bojie.a_great_day.data.firebase.FirebaseManager
import us.bojie.a_great_day.ui.MainActivity.Companion.EDIT_TASK_RESULT_OK

class AddEditTaskViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val firebaseManager: FirebaseManager
) : ViewModel() {

    var oldTaskName: String? = null
    val task = state.get<Task>("task")
    private val taskListSize = state.get<Int>("taskListSize")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskEstimate = state.get<String>("taskEstimate") ?: task?.estimate ?: ""
        set(value) {
            field = value
            state.set("taskEstimate", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        val taskPosition = task?.order ?: taskListSize
        val taskToSave =
            task?.copy(name = taskName, estimate = taskEstimate) ?: Task(
                name = taskName,
                estimate = taskEstimate,
                order = taskPosition
            )

        updateTask(taskToSave, oldTaskName)
    }

    fun onDeleteClick() {
        if (task != null) {
            deleteTask(task)
        } else {
            viewModelScope.launch {
                addEditTaskEventChannel.send(
                    AddEditTaskEvent.NavigateBackWithResult(
                        EDIT_TASK_RESULT_OK
                    )
                )
            }
        }
    }

    fun onSkipToNextDayClick() = viewModelScope.launch {
        if (task == null) return@launch
        if (firebaseManager.updateTaskToNextDay(task)) {
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }
    }


    private fun updateTask(task: Task, oldTaskName: String?) = viewModelScope.launch {
        if (firebaseManager.updateTask(task, oldTaskName = oldTaskName)) {
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }
    }

    private fun deleteTask(task: Task) = viewModelScope.launch {
        if (firebaseManager.deleteTask(task)) {
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}