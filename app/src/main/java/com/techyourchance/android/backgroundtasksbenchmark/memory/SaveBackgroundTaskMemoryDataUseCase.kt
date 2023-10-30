package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.database.entities.backgroundtasksmemory.AppMemoryInfoDb
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDb
import javax.inject.Inject

class SaveBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,

) {

    suspend fun saveData(label: String, iteration: Int, data: MutableMap<Int, BackgroundTaskMemoryData>) {
        for (task in data.keys) {
            val consumedMemory = data.values.toList()[task].consumedMemory
            backgroundTasksMemoryDao.upsert(
                BackgroundTasksMemoryDb(
                    0,
                    label,
                    iteration,
                    task,
                    AppMemoryInfoDb(0, consumedMemory)
                )
            )
        }
    }
}