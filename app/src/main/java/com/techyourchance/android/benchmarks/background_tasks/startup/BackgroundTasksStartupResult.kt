package com.techyourchance.android.benchmarks.background_tasks.startup

data class BackgroundTasksStartupResult(
    val averageStartupDurationNano: Long,
    val stdStartupTimeNano: Long,
    val threadsTimings: Map<Int, BackgroundTaskStartupData>
)