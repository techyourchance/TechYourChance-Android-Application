package com.techyourchance.android.benchmarks.shared_prefs

import com.techyourchance.android.common.maths.LinearFitCoefficients

data class SharedPrefsWriteResult(
    val entryIndexToAverageEditDurationsNano: Map<Int, Long>,
    val linearFitCoefficients: LinearFitCoefficients,
)