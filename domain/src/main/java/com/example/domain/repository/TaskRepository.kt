package com.example.domain.repository

import com.example.domain.model.Task
import java.time.LocalDateTime

interface TaskRepository {
    suspend fun getByDateRange(from: LocalDateTime, to: LocalDateTime): List<Task>
    suspend fun getById(id: Long): Task
    suspend fun save(task: Task): Long
    suspend fun delete(id: Long)
    suspend fun update(task: Task)
}