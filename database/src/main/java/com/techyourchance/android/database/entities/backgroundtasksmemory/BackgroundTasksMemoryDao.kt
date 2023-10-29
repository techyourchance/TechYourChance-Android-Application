package com.techyourchance.android.database.entities.backgroundtasksmemory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BackgroundTasksMemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BackgroundTasksMemoryDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entities: List<BackgroundTasksMemoryDb>)

    @Query("DELETE FROM backgroundTasksMemory")
    suspend fun deleteAll()

}