package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import javax.inject.Inject

class ClearBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,

) {

    suspend fun clearData() {
        backgroundTasksMemoryDao.deleteAll()
    }
}