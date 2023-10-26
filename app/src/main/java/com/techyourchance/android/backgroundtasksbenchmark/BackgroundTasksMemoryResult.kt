package com.techyourchance.android.backgroundtasksbenchmark

data class BackgroundTasksMemoryResult(
    val averageAppMemoryConsumption: Map<Int, BackgroundTaskMemoryData>,
    val tasksData: Map<Int, Map<Int, BackgroundTaskMemoryData>>,
)