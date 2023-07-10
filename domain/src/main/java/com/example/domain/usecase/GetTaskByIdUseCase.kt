package com.example.domain.usecase

import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository

class GetTaskByIdUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long): Task = repository.getById(id)
}