package com.example.presentation.tools

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RusDateTimeFormatter {
    private val months: Array<String>
        get() = arrayOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        );

    fun format(dateTime: LocalDateTime): String {
        val date = "${dateTime.dayOfMonth} ${months[dateTime.monthValue - 1]}"
        val time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        return "$date $time"
    }
}