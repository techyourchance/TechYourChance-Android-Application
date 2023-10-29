package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.common.application.AppMemoryInfoProvider

data class BackgroundTaskMemoryData(
    val appMemoryConsumption: AppMemoryInfoProvider.AppMemoryInfo,
) {
    companion object {
        val NULL_OBJECT = BackgroundTaskMemoryData(AppMemoryInfoProvider.AppMemoryInfo(0f, 0f))
    }
}