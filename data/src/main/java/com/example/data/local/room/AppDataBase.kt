package com.example.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.room.dao.TaskDao
import com.example.data.local.room.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "note_calendar.db"
    }
}