package com.techyourchance.android.benchmarks.background_tasks.memory

data class BackgroundTaskMemoryData(
    val timestamp: Long,
    val consumedMemory: Float,
) {
    companion object {
        val NULL_OBJECT = BackgroundTaskMemoryData(0, 0f)
    }
}