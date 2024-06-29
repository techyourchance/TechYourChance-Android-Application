package com.techyourchance.android.screens.benchmarks.sharedprefs

import com.techyourchance.android.benchmarks.sharedprefsedit.SharedPrefsEditResult
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class SharedPrefsBenchmarkViewMvc: BaseObservableViewMvc<SharedPrefsBenchmarkViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(
        resultWithCommit: SharedPrefsEditResult,
        resultWithApply: SharedPrefsEditResult,
    )
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}