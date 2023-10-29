package com.techyourchance.android.database

import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.RoomDatabase
import com.techyourchance.android.database.converters.AppMemoryInfoConverter
import com.techyourchance.android.database.converters.BackgroundTasksMemoryConverter
import com.techyourchance.android.database.entities.backgroundtasksmemory.AppMemoryInfoDb
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDb

@Database(
    entities = [
        AppMemoryInfoDb::class,
        BackgroundTasksMemoryDb::class
    ],
    version = 1
)
@TypeConverters(
    value = [
        AppMemoryInfoConverter::class,
        BackgroundTasksMemoryConverter::class,
    ]
)
internal abstract class MyRoomDatabase : RoomDatabase() {

    abstract val backgroundTasksMemoryDao: BackgroundTasksMemoryDao
}