package com.techyourchance.android.screens.threadsoverhead

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import com.techyourchance.android.threadsoverhead.ThreadsStartupBenchmarkUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ThreadsOverheadFragment : BaseFragment(), ThreadsOverheadViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var threadsStartupBenchmarkUseCase: ThreadsStartupBenchmarkUseCase

    private lateinit var viewMvc: ThreadsOverheadViewMvc

    private var benchmarkJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newThreadsOverheadViewMvc(container)
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
                val result = threadsStartupBenchmarkUseCase.runBenchmark()
                viewMvc.bindBenchmarkResults(result)
            } finally {
                viewMvc.showBenchmarkStopped()
            }
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        fun newInstance(): ThreadsOverheadFragment {
            return ThreadsOverheadFragment()
        }
    }
}