package com.techyourchance.android.common.device

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import javax.inject.Inject

class DeviceScreenMetricsProvider @Inject constructor(
    private val context: Context,
) {

    fun getScreenMetrics(): ScreenMetrics {
        val point = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        display.getRealSize(point)
        return ScreenMetrics(
            point.x, point.y, (context.resources.displayMetrics.density * 160f).toInt()
        )
    }
}