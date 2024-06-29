package com.techyourchance.android.screens.benchmarks.sharedprefs

import com.techyourchance.android.benchmarks.shared_prefs_write.SharedPrefsWriteResult
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class SharedPrefsBenchmarkViewMvc: BaseObservableViewMvc<SharedPrefsBenchmarkViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(
        prefValueLength: Int,
        resultWithCommit: SharedPrefsWriteResult,
        resultWithApply: SharedPrefsWriteResult,
    )
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}