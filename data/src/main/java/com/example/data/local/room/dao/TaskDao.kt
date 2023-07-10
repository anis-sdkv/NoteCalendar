package com.example.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.room.entity.TaskEntity
import com.example.domain.model.Task
import java.time.LocalDateTime

@Dao
interface TaskDao {
    @Query(
        "SELECT * FROM ${TaskEntity.TABLE_NAME} " +
                "WHERE startDate >= :from AND startDate <= :to " +
                "ORDER BY startDate ASC"
    )
    suspend fun getByDateRange(from: LocalDateTime, to: LocalDateTime): List<TaskEntity>

    @Query(
        "SELECT * FROM ${TaskEntity.TABLE_NAME} " +
                "WHERE id == :id"
    )
    suspend fun getById(id: Long): Task

    @Insert
    suspend fun save(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}