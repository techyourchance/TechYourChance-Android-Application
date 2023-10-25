package com.techyourchance.android.screens.threadsoverhead

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc
import com.techyourchance.android.backgroundstartup.BackgroundTasksStartupResult

abstract class ThreadsOverheadViewMvc: BaseObservableViewMvc<ThreadsOverheadViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(
        threadsResult: BackgroundTasksStartupResult,
        coroutinesResult: BackgroundTasksStartupResult,
        threadPoolResult: BackgroundTasksStartupResult,
    )
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}