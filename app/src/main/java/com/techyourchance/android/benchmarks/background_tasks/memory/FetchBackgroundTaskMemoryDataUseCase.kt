package com.techyourchance.android.benchmarks.background_tasks.memory

import com.techyourchance.android.common.maths.LinearFitCalculator
import com.techyourchance.android.common.maths.LinearFitCoefficients
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDb
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class FetchBackgroundTaskMemoryDataUseCase @Inject constructor(
    private val backgroundTasksMemoryDao: BackgroundTasksMemoryDao,
    private val linearFitCalculator: LinearFitCalculator,
) {

    suspend fun fetchData(label: String): BackgroundTasksMemoryResult {
        val dbEntities = backgroundTasksMemoryDao.fetchAllWithLabel(label)
        val sortedDbEntities = dbEntities.sortedBy { it.iterationNum * 100 + it.taskNum }
        val tasksData = preAllocatedDataStructures(sortedDbEntities)
        sortedDbEntities.forEach { dbEntity ->
            tasksData[dbEntity.iterationNum]!![dbEntity.taskNum] = BackgroundTaskMemoryData(
                dbEntity.memoryInfo.timestamp, dbEntity.memoryInfo.consumedMemory
            )
        }
        val averagedTasksData = computeAverageOfIterations(tasksData)
        val averageLinearFitCoefficients = calculateLinearFit(averagedTasksData)
        return BackgroundTasksMemoryResult(
            averagedTasksData,
            averageLinearFitCoefficients,
            tasksData,
        )
    }

    private fun preAllocatedDataStructures(sortedDbEntities: List<BackgroundTasksMemoryDb>): MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>> {
        val numIterations = sortedDbEntities.maxOf { it.iterationNum + 1 }
        val numTasks = sortedDbEntities.maxOf { it.taskNum + 1}

        val data = ConcurrentHashMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>(numIterations)

        for (iterationNum in 0 until numIterations) {
            val iterationData = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(numTasks)
            for (taskNum in 0 until numTasks) {
                iterationData[taskNum] = BackgroundTaskMemoryData.NULL_OBJECT
            }
            data[iterationNum] = iterationData
        }
        return data
    }

    private fun computeAverageOfIterations(tasksData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>): Map<Int, BackgroundTaskMemoryData> {
        val numIterations = tasksData.size
        val numTasks = tasksData[0]!!.size
        val averageTasksMemoryConsumptions = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(numTasks)
        for (taskNum in 0 until numTasks) {
            var sumTaskMemoryConsumption = 0f
            for (iterationNum in 0 until numIterations) {
                sumTaskMemoryConsumption += tasksData[iterationNum]!![taskNum]!!.consumedMemory
            }
            val averageTaskMemoryConsumption = sumTaskMemoryConsumption / numIterations
            averageTasksMemoryConsumptions[taskNum] = BackgroundTaskMemoryData(0, averageTaskMemoryConsumption)
        }
        return averageTasksMemoryConsumptions
    }


    private fun calculateLinearFit(data: Map<Int, BackgroundTaskMemoryData>): LinearFitCoefficients {
        val inputData = data.map { entry -> Pair(entry.key.toDouble(), entry.value.consumedMemory.toDouble()) }
        return linearFitCalculator.calculateLinearFit(inputData)
    }
}