package com.techyourchance.android.backgroundtasksbenchmark.memory

data class BackgroundTaskGroupsMemoryResult(
    val averageAppMemoryConsumption: Map<Int, BackgroundTaskMemoryData>,
    val averageLinearFitSlope: Float,
    val averageLinearFitIntercept: Float,
    val tasksData: Map<Int, Map<Int, BackgroundTaskMemoryData>>,
) {
    companion object {
        val NULL_OBJECT = BackgroundTaskGroupsMemoryResult(mapOf(), 0f, 0f, mapOf())
    }
}