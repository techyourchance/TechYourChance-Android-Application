package com.techyourchance.android.screens.threadsoverhead

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc
import com.techyourchance.android.threadsoverhead.ThreadsStartupResult

abstract class ThreadsOverheadViewMvc: BaseObservableViewMvc<ThreadsOverheadViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(result: ThreadsStartupResult)
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}