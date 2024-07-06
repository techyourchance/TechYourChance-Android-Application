package com.techyourchance.android.benchmarks.background_tasks.memory

import com.techyourchance.android.common.maths.LinearFitCoefficients

data class BackgroundTasksMemoryResult(
    val averageAppMemoryConsumption: Map<Int, BackgroundTaskMemoryData>,
    val averageLinearFitCoefficients: LinearFitCoefficients,
    val tasksData: Map<Int, Map<Int, BackgroundTaskMemoryData>>,
) {
    companion object {
        val NULL_OBJECT = BackgroundTasksMemoryResult(mapOf(), LinearFitCoefficients(0.0, 0.0), mapOf())
    }
}