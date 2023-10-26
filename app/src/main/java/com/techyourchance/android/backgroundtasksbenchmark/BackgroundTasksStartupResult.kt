package com.techyourchance.android.backgroundtasksbenchmark

data class BackgroundTasksStartupResult(
    val averageStartupDurationNano: Long,
    val stdStartupTimeNano: Long,
    val threadsTimings: Map<Int, BackgroundTaskStartupData>
)