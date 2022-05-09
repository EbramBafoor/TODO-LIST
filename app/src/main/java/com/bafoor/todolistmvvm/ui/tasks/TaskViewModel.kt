package com.bafoor.todolistmvvm.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bafoor.todolistmvvm.ADD_TASK_RESULT_OK
import com.bafoor.todolistmvvm.EDIT_TASK_RESULT_OK
import com.bafoor.todolistmvvm.data.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: ListDao,
    private val preferences: PreferencesManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val preferencesFlow = preferences.preferenceFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val taskFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortedOrder, filterPreferences.hideCompleted)
    }

    val task = taskFlow as LiveData<List<Task>>

    fun onSortOrderSelected(sortOrder: SortOrder) =
        viewModelScope.launch {
            preferences.updateSortedOrder(sortOrder)
        }

    fun onHideCompletedSelected(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferences.updateHideCompleted(hideCompleted)
        }
    }

    fun onUndoDeleteClick(task: Task) {
        viewModelScope.launch {
            taskDao.addTask(task)
        }
    }

    fun onTaskSelected(task: Task) {
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(completed = isChecked))
        }
    }

    fun onTaskSwiped(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
            taskEventChannel.send(TaskEvent.ShowUndoDeletedMessage(task))
        }
    }

    fun onAddNewTaskClick() {
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
        }
    }

    fun onAddEditTask(result: Int) {
        when(result) {
            ADD_TASK_RESULT_OK -> {
                showTaskConfirmatedMessage("message added")
            }
            EDIT_TASK_RESULT_OK -> {
                showTaskConfirmatedMessage("message updated")
            }
        }
    }

    private fun showTaskConfirmatedMessage(msg : String){
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.ShowTaskConfirmatedMessage(msg))
        }
    }

    fun onDeleteAllCompletedClicked(){
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
        }
    }
    sealed class TaskEvent {
        object NavigateToAddTaskScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task : Task) : TaskEvent()
        data class ShowUndoDeletedMessage(val task: Task) : TaskEvent()
        data class ShowTaskConfirmatedMessage(val msg : String) : TaskEvent()
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
    }
}