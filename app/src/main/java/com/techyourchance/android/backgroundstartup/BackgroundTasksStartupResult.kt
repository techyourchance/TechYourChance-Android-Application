package com.techyourchance.android.backgroundstartup

data class BackgroundTasksStartupResult(
    val averageStartupDurationNano: Long,
    val stdStartupTimeNano: Long,
    val threadsTimings: Map<Int, BackgroundTaskStartupData>
)