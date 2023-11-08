package com.techyourchance.android.backgroundtasksbenchmark.memory

data class BackgroundTaskMemoryData(
    val timestamp: Long,
    val consumedMemory: Float,
) {
    companion object {
        val NULL_OBJECT = BackgroundTaskMemoryData(0, 0f)
    }
}