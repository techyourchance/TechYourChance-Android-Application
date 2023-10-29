package com.techyourchance.android.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDb

@ProvidedTypeConverter
class BackgroundTasksMemoryConverter constructor(
    private val gson: Gson,
) {

    @TypeConverter
    fun fromString(serialized: String): BackgroundTasksMemoryDb {
        return gson.fromJson(serialized, BackgroundTasksMemoryDb::class.java)
    }

    @TypeConverter
    fun toString(dataStructure: BackgroundTasksMemoryDb): String {
        return gson.toJson(dataStructure)
    }
}