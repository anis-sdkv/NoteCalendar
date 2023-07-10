package com.example.presentation.screens.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Task
import com.example.domain.usecase.GetTaskByIdUseCase
import com.example.domain.usecase.SaveTaskUseCase
import com.example.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class TaskScreenState(
    val taskId: Long? = null,
    val taskName: String = "",
    val taskDescription: String = "",
    val startDate: LocalDateTime? = null,
    val finishDate: LocalDateTime? = null,
    val isEditMode: Boolean = false,
    val showStartDateSelector: Boolean = false,
    val showStartTimeSelector: Boolean = false,
    val showFinishDateSelector: Boolean = false,
    val showFinishTimeSelector: Boolean = false,
    val showErrorDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val errors: PersistentList<String> = persistentListOf()
)

sealed interface TaskScreenEvent {
    data class OnModeChange(val value: Boolean) : TaskScreenEvent
    data class OnNameChange(val value: String) : TaskScreenEvent
    data class OnDescriptionChange(val value: String) : TaskScreenEvent
    object OnNavigateBack : TaskScreenEvent
    object OnDateTimeClick : TaskScreenEvent
    object OnStartDateSelected : TaskScreenEvent
    object OnStartTimeSelected : TaskScreenEvent
    object OnFinishDateSelected : TaskScreenEvent
    data class OnFinishTimeSelected(val startDate: LocalDateTime, val finishDate: LocalDateTime) : TaskScreenEvent
    object OnDismissDateTime : TaskScreenEvent
    object OnDismissSave : TaskScreenEvent
    object OnConfirmSave : TaskScreenEvent
    object OnDismissError : TaskScreenEvent
}

sealed interface TaskScreenSideEffect {
    object NavigateBack : TaskScreenSideEffect
}

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<TaskScreenState> = MutableStateFlow(TaskScreenState())
    val state = _state.asStateFlow()

    private val _action = MutableSharedFlow<TaskScreenSideEffect?>()
    val action: SharedFlow<TaskScreenSideEffect?>
        get() = _action.asSharedFlow()

    init {
        when (val id = savedStateHandle.get<String?>(TASK_ID_KEY)) {
            NEW_TASK -> onLoadNew()
            is String -> loadTask(id.toLong())
            else -> throw IllegalStateException()
        }
    }

    fun event(event: TaskScreenEvent) {
        when (event) {
            is TaskScreenEvent.OnModeChange -> onModeChange(event.value)
            is TaskScreenEvent.OnNameChange -> onNameChange(event.value)
            is TaskScreenEvent.OnDescriptionChange -> onDescriptionChange(event.value)
            TaskScreenEvent.OnNavigateBack -> onNavigateBack()
            TaskScreenEvent.OnDismissDateTime -> dismissDateTime()
            TaskScreenEvent.OnDateTimeClick -> onDateTimeClick()
            TaskScreenEvent.OnStartTimeSelected -> showFinishDateSelector()
            TaskScreenEvent.OnStartDateSelected -> showStartTimeSelector()
            TaskScreenEvent.OnFinishDateSelected -> showFinishTimeSelector()
            is TaskScreenEvent.OnFinishTimeSelected -> setDeadlines(event.startDate, event.finishDate)
            TaskScreenEvent.OnDismissSave -> onDismissSave()
            TaskScreenEvent.OnConfirmSave -> onConfirmSave()
            TaskScreenEvent.OnDismissError -> onDismissError()
        }
    }

    private fun checkErrors(): List<String> {
        val errors = mutableListOf<String>()
        if (state.value.taskName.isEmpty())
            errors.add("Заголовок не может быть пустым.")

        if (state.value.taskDescription.isEmpty())
            errors.add("Описание не может быть пустым.")

        if (state.value.startDate == null || state.value.finishDate == null)
            errors.add("Выберите время начала и окончания.")
        return errors
    }

    private suspend fun saveChanges() {
        with(state.value) {
            val task = Task(
                id = taskId ?: 0,
                name = taskName,
                description = taskDescription,
                startDate = startDate ?: throw IllegalStateException(),
                finishDate = finishDate ?: throw IllegalStateException()
            )
            val id = if (taskId == null) saveTaskUseCase(task)
            else {
                updateTaskUseCase(task)
                taskId
            }
            _state.emit(state.value.copy(taskId = id))
        }
    }

    private fun onConfirmSave() {
        viewModelScope.launch {
            _action.emit(TaskScreenSideEffect.NavigateBack)
        }
    }

    private fun onDismissError() {
        viewModelScope.launch {
            _state.emit(state.value.copy(showErrorDialog = false))
        }
    }

    private fun onDismissSave() {
        viewModelScope.launch {
            _state.emit(state.value.copy(showSaveDialog = false))
        }
    }

    private fun showStartTimeSelector() {
        viewModelScope.launch {
            _state.emit(state.value.copy(showStartDateSelector = false, showStartTimeSelector = true))
        }
    }

    private fun showFinishDateSelector() {
        viewModelScope.launch {
            _state.emit(state.value.copy(showStartTimeSelector = false, showFinishDateSelector = true))
        }
    }

    private fun showFinishTimeSelector() {
        viewModelScope.launch {
            _state.emit(state.value.copy(showFinishDateSelector = false, showFinishTimeSelector = true))
        }
    }


    private fun setDeadlines(startDate: LocalDateTime, finishDate: LocalDateTime) {
        viewModelScope.launch {
            _state.emit(
                if (startDate.isBefore(finishDate))
                    state.value.copy(
                        showFinishTimeSelector = false,
                        startDate = startDate,
                        finishDate = finishDate
                    )
                else
                    state.value.copy(
                        showFinishTimeSelector = false,
                        showErrorDialog = true,
                        errors = persistentListOf("Дата начала должна быть раньше даты окончания")
                    )
            )
        }
    }

    private fun onDateTimeClick() {
        viewModelScope.launch { _state.emit(state.value.copy(isEditMode = true, showStartDateSelector = true)) }
    }

    private fun dismissDateTime() {
        viewModelScope.launch {
            _state.emit(
                state.value.copy(
                    showStartDateSelector = false,
                    showStartTimeSelector = false,
                    showFinishDateSelector = false,
                    showFinishTimeSelector = false,
                    startDate = null,
                    finishDate = null
                )
            )
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            if (state.value.isEditMode)
                _state.emit(state.value.copy(showSaveDialog = true))
            else
                _action.emit(TaskScreenSideEffect.NavigateBack)
        }
    }

    private fun onModeChange(toEditMode: Boolean) {
        viewModelScope.launch {
            val errors = checkErrors()
            if (toEditMode) {
                _state.emit(state.value.copy(isEditMode = true))
            } else if (errors.isEmpty()) {
                saveChanges()
                _state.emit(state.value.copy(isEditMode = false))
            } else _state.emit(state.value.copy(showErrorDialog = true, errors = errors.toPersistentList()))
        }
    }

    private fun onNameChange(value: String) {
        viewModelScope.launch { _state.emit(state.value.copy(taskName = value)) }
    }

    private fun onDescriptionChange(value: String) {
        viewModelScope.launch { _state.emit(state.value.copy(taskDescription = value)) }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            val task = getTaskByIdUseCase(id)
            _state.emit(
                state.value.copy(
                    taskId = task.id,
                    taskName = task.name,
                    taskDescription = task.description,
                    startDate = task.startDate,
                    finishDate = task.finishDate
                )
            )
        }
    }

    private fun onLoadNew() {
        viewModelScope.launch {
            _state.emit(state.value.copy(isEditMode = true))
        }
    }

    companion object {
        const val TASK_ID_KEY = "taskId"
        const val NEW_TASK = "new"
    }
}