package com.bafoor.todolistmvvm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDAO(): ListDao

    class CallBack @Inject constructor(
        private val database : Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope : CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDAO()

            applicationScope.launch {
                dao.addTask(Task("Wash the dishes"))
                dao.addTask(Task("Complete my home work",important = true))
                dao.addTask(Task("Call Elon Mask"))
                dao.addTask(Task("Finish my plans",completed = true))
                dao.addTask(Task("Gain more weight"))

            }
        }
    }
}