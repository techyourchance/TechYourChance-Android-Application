package com.techyourchance.android.benchmarks.background_tasks.memory

import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,

) {

    suspend fun clearData() {
        withContext(Dispatchers.Background) {
            backgroundTasksMemoryDao.deleteAll()
        }
    }
}