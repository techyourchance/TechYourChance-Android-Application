package com.techyourchance.android.benchmarks.backgroundtasks.startup

data class BackgroundTasksStartupResult(
    val averageStartupDurationNano: Long,
    val stdStartupTimeNano: Long,
    val threadsTimings: Map<Int, BackgroundTaskStartupData>
)