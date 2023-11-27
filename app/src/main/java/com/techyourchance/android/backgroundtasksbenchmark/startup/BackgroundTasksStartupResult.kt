package com.techyourchance.android.backgroundtasksbenchmark.startup

data class BackgroundTasksStartupResult(
    val averageStartupDurationNano: Long,
    val stdStartupTimeNano: Long,
    val threadsTimings: Map<Int, BackgroundTaskStartupData>
)