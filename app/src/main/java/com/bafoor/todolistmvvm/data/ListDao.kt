package com.bafoor.todolistmvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM task_table WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTaskSortedByName(searchQuery : String, hideCompleted : Boolean) : Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTaskSortedByDateCreated(searchQuery : String, hideCompleted : Boolean) : Flow<List<Task>>

    @Query("DELETE FROM task_table WHERE completed = 1")
    suspend fun deleteAllCompletedTask()

    fun getTasks(searchQuery: String, sortedOrder : SortOrder, hideCompleted : Boolean) : Flow<List<Task>> =
        when(sortedOrder){
            SortOrder.BY_NAME-> getTaskSortedByName(searchQuery, hideCompleted)
            SortOrder.BY_DATE-> getTaskSortedByName(searchQuery, hideCompleted)
        }
    //<--- if hideCompleted is TRUE show un completed task and the opposite is TRUE---->

}