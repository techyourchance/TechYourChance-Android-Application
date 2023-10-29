package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class FetchBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,

) {

    suspend fun fetchData(label: String): BackgroundTaskGroupsMemoryResult {
        val dbEntities = backgroundTasksMemoryDao.fetchAllWithLabel(label)
        val sortedDbEntities = dbEntities.sortedBy { it.iteration * 100 + it.tasksGroup }
        val tasksData = preAllocatedDataStructures()
        sortedDbEntities.forEach { dbEntity ->
            tasksData[dbEntity.iteration]!![dbEntity.tasksGroup] = BackgroundTaskMemoryData(
                dbEntity.memoryInfo.consumedMemory
            )
        }
        val averagedTasksData = computeAverage(tasksData)
        val (averageLinearFitSlope, averageLinearFitIntercept) = computeLinearFitCoefficients(averagedTasksData)
        return BackgroundTaskGroupsMemoryResult(
            averagedTasksData,
            averageLinearFitSlope,
            averageLinearFitIntercept,
            tasksData,
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
        val numTaskGroups = tasksData[0]!!.size
        val averageTasksMemoryConsumptions = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(
            numTaskGroups
        )
        for (taskNum in 0 until numTaskGroups) {
            var sumTaskMemoryConsumption = 0f
            for (iterationNum in 0 until tasksData.size) {
                sumTaskMemoryConsumption += tasksData[iterationNum]!![taskNum]!!.consumedMemory
            }
            val averageTaskMemoryConsumption = sumTaskMemoryConsumption / numTaskGroups
            averageTasksMemoryConsumptions[taskNum] = BackgroundTaskMemoryData(averageTaskMemoryConsumption)
        }
        return averageTasksMemoryConsumptions
    }

    private fun computeLinearFitCoefficients(tasksData: Map<Int, BackgroundTaskMemoryData>): Pair<Float, Float> {
        val n = tasksData.size
        var sumX = 0f
        var sumY = 0f
        var sumXY = 0f
        var sumXX = 0f

        for (i in 0 until n) {
            sumX += i
            sumY += tasksData[i]!!.consumedMemory
            sumXY += i * tasksData[i]!!.consumedMemory
            sumXX += i * i
        }

        val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        val intercept = (sumY - slope * sumX) / n

        return Pair(slope, intercept)
    }


}