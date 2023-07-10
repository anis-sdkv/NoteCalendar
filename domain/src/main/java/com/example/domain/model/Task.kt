package com.example.domain.model

import java.time.LocalDateTime

data class Task(
    val id: Long,
    val startDate: LocalDateTime,
    val finishDate: LocalDateTime,
    val name: String,
    val description: String
)