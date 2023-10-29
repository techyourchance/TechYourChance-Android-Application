package com.techyourchance.android.backgroundtasksbenchmark.memory

data class BackgroundTaskMemoryData(
    val consumedMemory: Float,
) {
    companion object {
        val NULL_OBJECT = BackgroundTaskMemoryData(0f)
    }
}