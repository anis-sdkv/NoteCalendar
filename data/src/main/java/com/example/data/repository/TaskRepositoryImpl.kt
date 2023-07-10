package com.example.data.repository

import com.example.data.local.room.dao.TaskDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun getByDateRange(from: LocalDateTime, to: LocalDateTime): List<Task> =
        taskDao.getByDateRange(from, to).map { it.toDomain() }

    override suspend fun getById(id: Long): Task =
        taskDao.getById(id)

    override suspend fun save(task: Task) =
        taskDao.save(task.toEntity())

    override suspend fun delete(id: Long) =
        taskDao.deleteTask(id)

    override suspend fun update(task: Task) =
        taskDao.updateTask(task.toEntity())

}