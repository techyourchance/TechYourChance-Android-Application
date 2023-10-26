package com.techyourchance.android.screens.backgroundtasksstartupbenchmark

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc
import com.techyourchance.android.backgroundtasksbenchmark.BackgroundTasksStartupResult

abstract class BackgroundTasksStartupBenchmarkViewMvc: BaseObservableViewMvc<BackgroundTasksStartupBenchmarkViewMvc.Listener>() {

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