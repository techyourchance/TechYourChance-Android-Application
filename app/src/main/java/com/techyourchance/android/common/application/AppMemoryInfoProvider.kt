package com.techyourchance.android.common.application

import android.os.Debug
import javax.inject.Inject

class AppMemoryInfoProvider @Inject constructor() {

    data class AppMemoryInfo(
        val heapMemoryKb: Float,
        val nativeMemoryKb: Float,
    )

    fun getAppMemoryConsumption(): AppMemoryInfo {
        val runtime = Runtime.getRuntime()
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        return AppMemoryInfo(
            memoryInfo.getMemoryStat("summary.java-heap").toFloat(),
            memoryInfo.getMemoryStat("summary.native-heap").toFloat(),
        )
    }
}