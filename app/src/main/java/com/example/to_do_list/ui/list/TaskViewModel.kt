package com.example.to_do_list.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.to_do_list.data.PreferencesManager
import com.example.to_do_list.data.SortOrder
import com.example.to_do_list.data.Task
import com.example.to_do_list.data.TaskDao
import com.example.to_do_list.ui.ADD_TASK_RESULT_OK
import com.example.to_do_list.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        private val preferencesManager: PreferencesManager,
        @Assisted private val state : SavedStateHandle

) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TaskEvent>()

    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private  val taskFlow = combine(
            searchQuery.asFlow(),
            preferencesFlow

    ){
        query, filterPreferences  ->
        Pair(query, filterPreferences)
    }.flatMapLatest {(query, filterPreferences) ->
        taskDao.getTask(query, filterPreferences.sortOrder, filterPreferences.hideCompleted )
    }

    val tasks = taskFlow.asLiveData()


    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }
    fun onHideCompletedSelected(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

     fun onTaskSelected(task: Task) = viewModelScope.launch{

         tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
     }
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwipe(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }
    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {

        tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int){
        when(result){
            ADD_TASK_RESULT_OK -> showTaskSaveConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSaveConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSaveConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteOnCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToDeleteOnCompleteScreen)
    }

    sealed class TaskEvent {

        object NavigateToAddTaskScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task:Task):TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String): TaskEvent()
        object NavigateToDeleteOnCompleteScreen: TaskEvent()
    }

}

