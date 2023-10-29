package com.techyourchance.android.screens.backgroundtasksmemorybenchmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.backgroundtasksbenchmark.memory.BackgroundTasksMemoryBenchmarkPhase
import com.techyourchance.android.backgroundtasksbenchmark.memory.BackgroundTasksMemoryBenchmarkUseCase
import com.techyourchance.android.common.restart.RestartAppUseCase
import com.techyourchance.android.screens.common.ScreenSpec
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
    @Inject lateinit var restartAppUseCase: RestartAppUseCase

    private lateinit var viewMvc: BackgroundTasksMemoryBenchmarkViewMvc

    private var startBenchmark = false
    private var startBenchmarkPhase = BackgroundTasksMemoryBenchmarkPhase.THREADS
    private var startBenchmarkIterationNum = 0

    private var benchmarkJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
        val args = requireArguments()
        startBenchmark = args.getBoolean(ARG_START_BENCHMARK)
        startBenchmarkPhase = args.getSerializable(ARG_START_BENCHMARK_PHASE) as BackgroundTasksMemoryBenchmarkPhase
        startBenchmarkIterationNum = args.getInt(ARG_START_BENCHMARK_ITERATION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newBackgroundTasksMemoryBenchmarkViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        viewMvc.showBenchmarkStopped()
        if (startBenchmark) {
            startBenchmark()
        }
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
                relaunchAppAndStartBenchmark()
            }
        } ?: relaunchAppAndStartBenchmark()
    }

    private fun relaunchAppAndStartBenchmark() {
        restartAppUseCase.restartAppOnScreen(
            ScreenSpec.BackgroundTasksMemoryBenchmark(startBenchmark = true)
        )
    }

    private fun startBenchmark() {
        benchmarkJob = coroutineScope.launch {
            try {
                viewMvc.showBenchmarkStarted()
                val result = backgroundTasksMemoryBenchmarkUseCase.runBenchmark(startBenchmarkPhase, startBenchmarkIterationNum)
                viewMvc.bindBenchmarkResults(
                    result.numTasksInGroup,
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
        const val ARG_START_BENCHMARK = "ARG_START_BENCHMARK"
        const val ARG_START_BENCHMARK_PHASE = "ARG_START_BENCHMARK_PHASE"
        const val ARG_START_BENCHMARK_ITERATION = "ARG_START_BENCHMARK_ITERATION"

        fun newInstance(screenSpec: ScreenSpec.BackgroundTasksMemoryBenchmark): BackgroundTasksMemoryBenchmarkFragment {
            val args = Bundle(3)
            args.putBoolean(ARG_START_BENCHMARK, screenSpec.startBenchmark)
            args.putSerializable(ARG_START_BENCHMARK_PHASE, screenSpec.startBenchmarkPhase)
            args.putInt(ARG_START_BENCHMARK_ITERATION, screenSpec.startBenchmarkIteration)
            val fragment = BackgroundTasksMemoryBenchmarkFragment()
            fragment.arguments = args
            return fragment
        }
    }
}