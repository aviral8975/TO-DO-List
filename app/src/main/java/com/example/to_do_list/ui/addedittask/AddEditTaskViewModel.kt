package com.example.to_do_list.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_list.data.Task
import com.example.to_do_list.data.TaskDao
import com.example.to_do_list.ui.ADD_TASK_RESULT_OK
import com.example.to_do_list.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskname = state.get<String>("taskname") ?: task?.name ?: ""
    set(value) {
        field = value
        state.set("taskname", value)
    }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.imprtance ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick(){
        if(taskname.isBlank()){
            showInnvalidInputMsg("Name cannot be empty!!")
            return
        }
        if (task != null) {
            val updatedTask = task.copy(name = taskname, imprtance = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskname, imprtance = taskImportance)
            createtask(newTask)
        }
    }

    private fun createtask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackResult(ADD_TASK_RESULT_OK))
    }
    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInnvalidInputMsg(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent{
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackResult(val result: Int) : AddEditTaskEvent()
    }


}