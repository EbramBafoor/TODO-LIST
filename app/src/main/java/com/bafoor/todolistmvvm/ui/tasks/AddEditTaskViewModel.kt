package com.bafoor.todolistmvvm.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation.findNavController
import com.bafoor.todolistmvvm.ADD_TASK_RESULT_OK
import com.bafoor.todolistmvvm.EDIT_TASK_RESULT_OK
import com.bafoor.todolistmvvm.data.ListDao
import com.bafoor.todolistmvvm.data.Task
import com.bafoor.todolistmvvm.ui.fragment.AddFragmentDirections
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao : ListDao,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    val task = savedStateHandle.get<Task>("task")

    var taskName = savedStateHandle.get<String>("taskName") ?: task?.name ?: ""
    set(value) {
        field = value
        savedStateHandle.set("taskName", value)
    }

    var taskImportance = savedStateHandle.get<Boolean>("taskImportance") ?: task?.important ?: false
    set(value) {
        field = value
        savedStateHandle.set("taskImportance", value)
    }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick(){
        if(taskName.isBlank()){
            showInvalidInputMessage("name cannot be empty")
            return
        }
        if (task != null){
            val updateTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updateTask)
        } else{
            val newTask = Task(name = taskName, important = taskImportance)
            createNewTask(newTask)
        }
    }

    private fun showInvalidInputMessage(text: String) {
        viewModelScope.launch {
            addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
        }
    }

    private fun updateTask(updateTask: Task){
        viewModelScope.launch {
            taskDao.updateTask(updateTask)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK)) //2
        }
    }

    private fun createNewTask(newTask : Task){
        viewModelScope.launch {
            taskDao.addTask(newTask)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK)) //1
        }
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val txt : String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result : Int) : AddEditTaskEvent()
    }

}




