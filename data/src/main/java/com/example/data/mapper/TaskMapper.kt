package com.example.data.mapper

import com.example.data.local.room.entity.TaskEntity
import com.example.domain.model.Task

internal fun TaskEntity.toDomain() =
    Task(
        this.id,
        this.startDate,
        this.finishDate,
        this.name,
        this.description
    )

internal fun Task.toEntity() =
    TaskEntity(
        this.startDate,
        this.finishDate,
        this.name,
        this.description,
        this.id
    )

