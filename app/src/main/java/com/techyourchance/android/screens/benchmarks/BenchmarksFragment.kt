package com.techyourchance.android.screens.benchmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import javax.inject.Inject

class BenchmarksFragment : BaseFragment(), BenchmarksViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var viewMvc: BenchmarksViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::viewMvc.isInitialized) {
            viewMvc = viewMvcFactory.newBenchmarksViewMvc(container)
            viewMvc.bindDestinations(getDestinations())
        }
        return viewMvc.getRootView()
    }

    private fun getDestinations(): List<FromBenchmarksDestination> {
        return listOf(
            FromBenchmarksDestination(
                getString(R.string.screen_background_tasks_startup_benchmark),
                ScreenSpec.BackgroundTasksStartupBenchmark
            ),
            FromBenchmarksDestination(
                getString(R.string.screen_background_tasks_memory_benchmark),
                ScreenSpec.BackgroundTasksMemoryBenchmark()
            ),
        )
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
    }

    override fun onDestinationClicked(destination: FromBenchmarksDestination) {
        screensNavigator.toScreen(destination.screenSpec)
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        fun newInstance(): BenchmarksFragment {
            return BenchmarksFragment()
        }
    }
}