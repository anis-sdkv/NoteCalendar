package com.example.data.local.room

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.R.raw.tasks
import com.example.data.local.room.entity.TaskEntity
import com.example.domain.model.Task
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FillCallback(
    private val context: Context
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        val json = context.resources
            .openRawResource(tasks)
            .bufferedReader()
            .use { it.readText() }

        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): LocalDateTime {
                    val instant = Instant.ofEpochMilli(json.asJsonPrimitive.asLong)
                    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                }
            })
            .create()

        val timeConverter = Converters()

        val tasks = gson.fromJson(json, Array<Task>::class.java)

        val query = tasks.joinToString(
            ",",
            "INSERT INTO ${TaskEntity.TABLE_NAME} (name, description, startDate, finishDate) VALUES ",
            ";"
        ) {
            "('${it.name.replace("'", "''")}', '${it.description.replace("'", "''")}', ${
                timeConverter.dateToTimestamp(it.startDate)
            }, ${
                timeConverter.dateToTimestamp(it.finishDate)
            })"
        }

        val res = db.query("SELECT * FROM tasks")

        db.execSQL(query)

        println()
    }
}