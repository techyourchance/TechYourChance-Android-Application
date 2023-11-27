package com.techyourchance.android.screens.benchmarks.backgroundtasksmemorybenchmark

import com.techyourchance.android.backgroundtasksbenchmark.memory.BackgroundTasksMemoryResult
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class BackgroundTasksMemoryBenchmarkViewMvc: BaseObservableViewMvc<BackgroundTasksMemoryBenchmarkViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(
        threadsResult: BackgroundTasksMemoryResult,
        coroutinesResult: BackgroundTasksMemoryResult,
        threadPoolResult: BackgroundTasksMemoryResult,
    )
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}