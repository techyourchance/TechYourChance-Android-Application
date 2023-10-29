package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.common.application.AppMemoryInfo

data class BackgroundTaskMemoryData(
    val appMemoryInfo: AppMemoryInfo,
) {
    companion object {
        val NULL_OBJECT = BackgroundTaskMemoryData(AppMemoryInfo(0f, 0f))
    }
}