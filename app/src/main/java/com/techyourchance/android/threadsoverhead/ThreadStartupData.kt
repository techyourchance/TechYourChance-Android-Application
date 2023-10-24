package com.techyourchance.android.threadsoverhead

data class ThreadStartupData(
    val threadName: String,
    val startupDurationNano: Long,
)