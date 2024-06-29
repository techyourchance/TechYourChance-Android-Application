package com.techyourchance.android.benchmarks.backgroundtasks.memory

import com.techyourchance.android.common.datetime.DateTimeProvider
import com.techyourchance.android.database.entities.backgroundtasksmemory.AppMemoryInfoDb
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDb
import javax.inject.Inject

class SaveBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,
    private val dateTimeProvider: DateTimeProvider,
) {

    suspend fun saveData(label: String, iteration: Int, data: MutableMap<Int, BackgroundTaskMemoryData>) {
        for (task in data.keys) {
            val backgroundTaskMemoryData = data.values.toList()[task]
            val consumedMemory = backgroundTaskMemoryData.consumedMemory
            val timestamp = backgroundTaskMemoryData.timestamp
            backgroundTasksMemoryDao.upsert(
                BackgroundTasksMemoryDb(
                    0,
                    label,
                    iteration,
                    task,
                    AppMemoryInfoDb(0, timestamp, consumedMemory)
                )
            )
        }
    }
}