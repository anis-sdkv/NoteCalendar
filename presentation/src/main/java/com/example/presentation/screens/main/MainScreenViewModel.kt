package com.example.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Task
import com.example.domain.usecase.GetTasksByDateUseCase
import com.example.presentation.tools.buildPersistenceList
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class MainScreenState(
    val currentDay: LocalDate = LocalDate.now(),
    val dayTasks: PersistentList<PersistentList<Task>> = buildPersistenceList {
        repeat(MainScreenViewModel.HOURS.size) {
            this.add(persistentListOf())
        }
    },
    val calendarVisible: Boolean = false
)

sealed interface MainScreenEvent {
    data class OnDateSelected(val date: LocalDate) : MainScreenEvent
    data class OnTaskClick(val id: Long) : MainScreenEvent
    data class OnCalendarVisibilityChange(val value: Boolean) : MainScreenEvent
    object OnCreateNewTaskClick : MainScreenEvent
    object OnCurrentDayClick : MainScreenEvent
    object OnLaunch : MainScreenEvent
}

sealed interface MainScreenSideEffect {
    data class NavigateToTaskScreen(val id: Long) : MainScreenSideEffect
    object NavigateToCreateTaskScreen : MainScreenSideEffect
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getTasksByDateUseCase: GetTasksByDateUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<MainScreenState> = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    private val _action = MutableSharedFlow<MainScreenSideEffect?>()
    val action: SharedFlow<MainScreenSideEffect?>
        get() = _action.asSharedFlow()

    fun event(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.OnDateSelected -> selectDate(event.date)
            is MainScreenEvent.OnTaskClick -> onTaskClick(event.id)
            is MainScreenEvent.OnCalendarVisibilityChange -> onCalendarVisibilityChange(event.value)
            MainScreenEvent.OnCreateNewTaskClick -> onCreateNewTaskClick()
            MainScreenEvent.OnCurrentDayClick -> onCurrentDayClick()
            MainScreenEvent.OnLaunch -> loadDay(state.value.currentDay)
        }
    }

    private fun onCurrentDayClick() {
        val targetDate = LocalDate.now().withDayOfMonth(1)
        val calendarVisible = if (!state.value.calendarVisible) true
        else targetDate != state.value.currentDay

        viewModelScope.launch {
            _state.emit(
                state.value.copy(
                    currentDay = targetDate,
                    calendarVisible = calendarVisible
                )
            )
        }
    }

    private fun onCalendarVisibilityChange(value: Boolean) {
        viewModelScope.launch { _state.emit(state.value.copy(calendarVisible = value)) }
    }

    private fun onCreateNewTaskClick() {
        viewModelScope.launch { _action.emit(MainScreenSideEffect.NavigateToCreateTaskScreen) }
    }

    private fun onTaskClick(id: Long) {
        viewModelScope.launch { _action.emit(MainScreenSideEffect.NavigateToTaskScreen(id)) }
    }

    private fun selectDate(date: LocalDate) {
        viewModelScope.launch { _state.emit(state.value.copy(currentDay = date)) }
        loadDay(date)
    }

    private fun loadDay(date: LocalDate) {
        viewModelScope.launch {
            val tasks = getTasksByDateUseCase(date)
            _state.emit(
                state.value.copy(
                    dayTasks = generateDayTable(tasks)
                )
            )
        }
    }

    private fun generateDayTable(tasks: List<Task>): PersistentList<PersistentList<Task>> {
        val dayTable = List(HOURS.size) { mutableListOf<Task>() }
        for (task in tasks) {
            val hour = task.startDate.hour
            dayTable[hour].add(task)
        }
        return dayTable
            .map { it.toPersistentList() }
            .toPersistentList()
    }

    companion object {
        val HOURS = Array(24) {
            LocalTime.MIN.plusHours(it.toLong()).format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }
}