package com.techyourchance.template.common.device

import java.io.Serializable

data class ScreenMetrics(
    val width: Int,
    val height: Int,
    val dpi: Int
) : Serializable