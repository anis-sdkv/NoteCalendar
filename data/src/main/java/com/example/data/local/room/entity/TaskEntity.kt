package com.example.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(TaskEntity.TABLE_NAME)
data class TaskEntity(
    val startDate: LocalDateTime,
    val finishDate: LocalDateTime,
    val name: String,
    val description: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
) {
    companion object {
        const val TABLE_NAME = "tasks"
    }
}