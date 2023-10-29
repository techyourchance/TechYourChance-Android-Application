package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.common.application.AppMemoryInfo
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class FetchBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,

) {

    suspend fun fetchData(label: String): BackgroundTasksMemoryResult {
        val dbEntities = backgroundTasksMemoryDao.fetchAllWithLabel(label)
        val sortedDbEntities = dbEntities.sortedBy { it.iteration * 100 + it.tasksGroup }
        val tasksData = preAllocatedDataStructures()
        sortedDbEntities.forEach { dbEntity ->
            tasksData[dbEntity.iteration]!![dbEntity.tasksGroup] = BackgroundTaskMemoryData(
                AppMemoryInfo(
                    dbEntity.memoryInfo.heapMemoryKb,
                    dbEntity.memoryInfo.nativeMemoryKb
                )
            )
        }
        return BackgroundTasksMemoryResult(
            computeAverage(tasksData),
            tasksData
        )
    }

    private fun preAllocatedDataStructures(): MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>> {
        val data = ConcurrentHashMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>(
            BackgroundTasksMemoryBenchmarkUseCase.NUM_ITERATIONS
        )
        for (iterationNum in 0 until BackgroundTasksMemoryBenchmarkUseCase.NUM_ITERATIONS) {
            val iterationData = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(BackgroundTasksMemoryBenchmarkUseCase.NUM_TASK_GROUPS)
            for (taskNum in 0 until BackgroundTasksMemoryBenchmarkUseCase.NUM_TASK_GROUPS) {
                iterationData[taskNum] = BackgroundTaskMemoryData.NULL_OBJECT
            }
            data[iterationNum] = iterationData
        }
        return data
    }

    private fun computeAverage(tasksData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>): Map<Int, BackgroundTaskMemoryData> {
        val averageTasksMemoryConsumptions = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(
            BackgroundTasksMemoryBenchmarkUseCase.NUM_TASK_GROUPS
        )
        for (taskNum in 0 until BackgroundTasksMemoryBenchmarkUseCase.NUM_TASK_GROUPS) {
            var sumTaskHeapMemoryConsumption = 0f
            var sumTaskNativeMemoryConsumption = 0f
            for (iterationNum in 0 until BackgroundTasksMemoryBenchmarkUseCase.NUM_ITERATIONS) {
                sumTaskHeapMemoryConsumption += tasksData[iterationNum]!![taskNum]!!.appMemoryInfo.heapMemoryKb
                sumTaskNativeMemoryConsumption += tasksData[iterationNum]!![taskNum]!!.appMemoryInfo.nativeMemoryKb
            }
            val averageHeapTaskMemoryConsumption = sumTaskHeapMemoryConsumption / BackgroundTasksMemoryBenchmarkUseCase.NUM_TASK_GROUPS
            val averageNativeTaskMemoryConsumption = sumTaskNativeMemoryConsumption / BackgroundTasksMemoryBenchmarkUseCase.NUM_TASK_GROUPS
            averageTasksMemoryConsumptions[taskNum] = BackgroundTaskMemoryData(
                AppMemoryInfo(
                    averageHeapTaskMemoryConsumption,
                    averageNativeTaskMemoryConsumption
                )
            )
        }
        return averageTasksMemoryConsumptions
    }

}