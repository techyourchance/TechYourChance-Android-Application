package com.techyourchance.android.database.entities.backgroundtasksmemory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appMemoryInfo")
data class AppMemoryInfoDb(
    @field:ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
    @field:ColumnInfo(name = "heapMemoryKb") val heapMemoryKb: Float,
    @field:ColumnInfo(name = "nativeMemoryKb") val nativeMemoryKb: Float,
)