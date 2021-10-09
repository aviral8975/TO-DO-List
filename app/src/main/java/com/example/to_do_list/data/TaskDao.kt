package com.example.to_do_list.data

import androidx.room.*

import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTask(query: String, sortOrder: SortOrder, hideCompleted: Boolean ): Flow<List<Task>> =
            when(sortOrder){
                SortOrder.BY_DATE -> getTasksSortByDateCreated(query, hideCompleted)
                SortOrder.BY_NAME -> getTasksSortByName(query, hideCompleted)
            }

    @Query("SELECT * FROM task_table WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY imprtance DESC, name")
    fun getTasksSortByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY imprtance DESC, created")
    fun getTasksSortByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE completed = 1")
    suspend fun deleteCompletedTask()
}
