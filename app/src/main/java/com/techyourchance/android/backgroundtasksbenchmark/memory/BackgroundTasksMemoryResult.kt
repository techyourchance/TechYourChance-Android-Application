package com.techyourchance.android.backgroundtasksbenchmark.memory

data class BackgroundTasksMemoryResult(
    val averageAppMemoryConsumption: Map<Int, BackgroundTaskMemoryData>,
    val tasksData: Map<Int, Map<Int, BackgroundTaskMemoryData>>,
)