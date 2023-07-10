package com.example.notecalendar.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.room.AppDataBase
import com.example.data.local.room.FillCallback
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindModule {
    @Binds
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}

@Module
@InstallIn(SingletonComponent::class)
class DataProvideModule {

    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDataBase::class.java, AppDataBase.DATABASE_NAME)
            .addCallback(FillCallback(context))
            .build()

    @Provides
    fun provideTaskDao(dataBase: AppDataBase) = dataBase.getTaskDao()
}