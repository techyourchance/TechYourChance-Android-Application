package com.techyourchance.android.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.techyourchance.android.database.entities.backgroundtasksmemory.AppMemoryInfoDb

@ProvidedTypeConverter
class AppMemoryInfoConverter(private val gson: Gson) {

    @TypeConverter
    fun fromString(serialized: String): AppMemoryInfoDb {
        return gson.fromJson(serialized, AppMemoryInfoDb::class.java)
    }

    @TypeConverter
    fun toString(entity: AppMemoryInfoDb): String {
        return gson.toJson(entity)
    }
}