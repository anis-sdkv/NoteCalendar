package com.example.domain.usecase

import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository

class SaveTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Long = repository.save(task)
}