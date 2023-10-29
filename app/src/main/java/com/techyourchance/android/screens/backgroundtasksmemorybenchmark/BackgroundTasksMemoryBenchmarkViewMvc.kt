package com.techyourchance.android.screens.backgroundtasksmemorybenchmark

import com.techyourchance.android.backgroundtasksbenchmark.memory.BackgroundTasksMemoryResult
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class BackgroundTasksMemoryBenchmarkViewMvc: BaseObservableViewMvc<BackgroundTasksMemoryBenchmarkViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(
        numTasksInGroup: Int,
        threadsResult: BackgroundTasksMemoryResult,
        coroutinesResult: BackgroundTasksMemoryResult,
        threadPoolResult: BackgroundTasksMemoryResult,
    )
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}