package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.database.entities.backgroundtasksmemory.AppMemoryInfoDb
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDb
import javax.inject.Inject

class SaveBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,

) {

    suspend fun saveData(label: String, iteration: Int, data: MutableMap<Int, BackgroundTaskMemoryData>) {
        for (tasksGroup in data.keys) {
            val consumedMemory = data.values.toList()[tasksGroup].consumedMemory
            backgroundTasksMemoryDao.upsert(
                BackgroundTasksMemoryDb(
                    0,
                    label,
                    iteration,
                    tasksGroup,
                    AppMemoryInfoDb(0, consumedMemory)
                )
            )
        }
    }
}