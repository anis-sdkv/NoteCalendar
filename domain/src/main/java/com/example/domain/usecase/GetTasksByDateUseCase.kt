package com.example.domain.usecase

import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import java.time.LocalDate
import java.time.LocalTime

class GetTasksByDateUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(date: LocalDate): List<Task> =
        repository.getByDateRange(date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX))
}