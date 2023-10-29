package com.techyourchance.android.database.entities.backgroundtasksmemory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backgroundTasksMemory")
data class BackgroundTasksMemoryDb(
    @field:ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
    @field:ColumnInfo(name = "label") val label: String,
    @field:ColumnInfo(name = "iteration") val iteration: Int,
    @field:ColumnInfo(name = "tasksGroup") val tasksGroup: Int,
    @field:ColumnInfo(name = "memoryInfo") val memoryInfo: AppMemoryInfoDb,
)