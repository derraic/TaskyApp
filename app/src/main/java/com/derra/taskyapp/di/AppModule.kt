package com.derra.taskyapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.derra.taskyapp.ReminderNotificationService
import com.derra.taskyapp.SyncWorker
import com.derra.taskyapp.data.NotificationAlarmScheduler
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.TaskyRepositoryImpl
import com.derra.taskyapp.data.remote.TaskyApi
import com.derra.taskyapp.data.room.TaskyDao
import com.derra.taskyapp.data.room.TaskyDatabase
import com.derra.taskyapp.util.UserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): TaskyApi {
        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Replace with your preferred converter factory
            .build()
            .create(TaskyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserManager(@ApplicationContext context: Context): UserManager {
        return UserManager(context)
    }

    @Provides
    @Singleton
    fun provideTaskyDatabase(app: Application): TaskyDatabase {
        return Room.databaseBuilder(
            app,
            TaskyDatabase::class.java, "tasky.db",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }


    @Provides
    @Singleton
    fun provideTaskyRepository(taskyApi: TaskyApi, db: TaskyDatabase, @ApplicationContext context: Context): TaskyRepository {
        return TaskyRepositoryImpl(taskyApi, db.dao, context)
    }


    @Provides
    @Singleton
    fun provideNotificationAlarmScheduler(@ApplicationContext context: Context): NotificationAlarmScheduler {
        return NotificationAlarmScheduler(context)
    }






}