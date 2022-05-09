package com.bafoor.todolistmvvm.data

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ActivityComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callBack: TaskDatabase.CallBack
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_table")
        .fallbackToDestructiveMigration()
        .addCallback(callBack)
        .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDAO()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
