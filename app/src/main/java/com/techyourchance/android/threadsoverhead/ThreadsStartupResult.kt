package com.techyourchance.android.threadsoverhead

data class ThreadsStartupResult(
    val averageStartupDurationNano: Long,
    val stdStartupTimeNano: Long,
    val threadsTimings: Map<Int, ThreadStartupData>
)