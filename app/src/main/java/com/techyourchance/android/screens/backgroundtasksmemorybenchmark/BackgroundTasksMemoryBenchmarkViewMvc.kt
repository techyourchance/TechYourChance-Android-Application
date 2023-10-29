package com.techyourchance.android.screens.backgroundtasksmemorybenchmark

import com.techyourchance.android.backgroundtasksbenchmark.memory.BackgroundTaskGroupsMemoryResult
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class BackgroundTasksMemoryBenchmarkViewMvc: BaseObservableViewMvc<BackgroundTasksMemoryBenchmarkViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleBenchmarkClicked()
    }

    abstract fun bindBenchmarkResults(
        numTasksInGroup: Int,
        threadsResult: BackgroundTaskGroupsMemoryResult,
        coroutinesResult: BackgroundTaskGroupsMemoryResult,
        threadPoolResult: BackgroundTaskGroupsMemoryResult,
    )
    abstract fun showBenchmarkStarted()
    abstract fun showBenchmarkStopped()
}