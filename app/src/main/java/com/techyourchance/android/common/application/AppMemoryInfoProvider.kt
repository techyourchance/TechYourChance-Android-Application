package com.techyourchance.android.common.application

import android.os.Debug
import javax.inject.Inject

class AppMemoryInfoProvider @Inject constructor() {

    fun getAppMemoryConsumption(): AppMemoryInfo {
        val runtime = Runtime.getRuntime()
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        return AppMemoryInfo(
            memoryInfo.getMemoryStat("summary.java-heap").toFloat(),
            memoryInfo.getMemoryStat("summary.native-heap").toFloat(),
            memoryInfo.getMemoryStat("summary.stack").toFloat(),
        )
    }
}