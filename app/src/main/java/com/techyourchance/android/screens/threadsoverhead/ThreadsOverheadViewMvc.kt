package com.techyourchance.android.screens.threadsoverhead

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc
import com.techyourchance.android.threadsoverhead.BackgroundTasksStartupResult

abstract class ThreadsOverheadViewMvc: BaseObservableViewMvc<ThreadsOverheadViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(
        threadsResult: BackgroundTasksStartupResult,
        coroutinesResult: BackgroundTasksStartupResult,
    )
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}