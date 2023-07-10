package com.example.notecalendar.di

import com.example.domain.repository.TaskRepository
import com.example.domain.usecase.GetTaskByIdUseCase
import com.example.domain.usecase.GetTasksByDateUseCase
import com.example.domain.usecase.SaveTaskUseCase
import com.example.domain.usecase.UpdateTaskUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {
    @Provides
    fun provideGetTaskByIdUseCase(taskRepository: TaskRepository): GetTaskByIdUseCase =
        GetTaskByIdUseCase(taskRepository)

    @Provides
    fun provideGetTasksByDateUseCase(taskRepository: TaskRepository): GetTasksByDateUseCase =
        GetTasksByDateUseCase(taskRepository)

    @Provides
    fun provideSaveTaskUseCase(taskRepository: TaskRepository): SaveTaskUseCase =
        SaveTaskUseCase(taskRepository)

    @Provides
    fun provideUpdateTaskUseCase(taskRepository: TaskRepository): UpdateTaskUseCase =
        UpdateTaskUseCase(taskRepository)
}