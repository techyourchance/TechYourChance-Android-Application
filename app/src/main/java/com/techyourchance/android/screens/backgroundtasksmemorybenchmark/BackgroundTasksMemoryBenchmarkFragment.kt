package com.techyourchance.android.screens.backgroundtasksmemorybenchmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.backgroundtasksbenchmark.BackgroundTasksMemoryBenchmarkUseCase
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class BackgroundTasksMemoryBenchmarkFragment : BaseFragment(), BackgroundTasksMemoryBenchmarkViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var backgroundTasksMemoryBenchmarkUseCase: BackgroundTasksMemoryBenchmarkUseCase

    private lateinit var viewMvc: BackgroundTasksMemoryBenchmarkViewMvc

    private var benchmarkJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newBackgroundTasksMemoryBenchmarkViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        viewMvc.showBenchmarkStopped()
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        benchmarkJob?.cancel()
    }

    override fun onToggleBenchmarkClicked() {
        benchmarkJob?.let {
            if (it.isActive) {
                it.cancel()
            } else {
                startBenchmark()
            }
        } ?: startBenchmark()
    }

    private fun startBenchmark() {
        benchmarkJob = coroutineScope.launch {
            try {
                viewMvc.showBenchmarkStarted()
                val result = backgroundTasksMemoryBenchmarkUseCase.runBenchmark()
                viewMvc.bindBenchmarkResults(
                    result.threadsResult,
                    result.coroutinesResult,
                    result.threadPoolResult,
                )
            } finally {
                viewMvc.showBenchmarkStopped()
            }
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        fun newInstance(): BackgroundTasksMemoryBenchmarkFragment {
            return BackgroundTasksMemoryBenchmarkFragment()
        }
    }
}