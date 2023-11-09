package com.techyourchance.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techyourchance.android.database.converters.AppMemoryInfoConverter
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDb

@Database(
    entities = [
        BackgroundTasksMemoryDb::class
    ],
    version = 2
)
@TypeConverters(
    value = [
        AppMemoryInfoConverter::class,
    ]
)
internal abstract class MyRoomDatabase : RoomDatabase() {

    abstract val backgroundTasksMemoryDao: BackgroundTasksMemoryDao
}