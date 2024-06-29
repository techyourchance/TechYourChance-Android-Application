package com.techyourchance.android.screens.benchmarks.benchmarkslist

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

class BenchmarksListFragment : BaseFragment(), BenchmarksListViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var viewMvc: BenchmarksListViewMvc

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

    private fun getDestinations(): List<FromBenchmarksListDestination> {
        return listOf(
            FromBenchmarksListDestination(
                getString(R.string.screen_background_tasks_startup_benchmark),
                ScreenSpec.BackgroundTasksStartupBenchmark
            ),
            FromBenchmarksListDestination(
                getString(R.string.screen_background_tasks_memory_benchmark),
                ScreenSpec.BackgroundTasksMemoryBenchmark()
            ),
            FromBenchmarksListDestination(
                getString(R.string.screen_shared_prefs_benchmark),
                ScreenSpec.SharedPrefsBenchmark
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

    override fun onDestinationClicked(destination: FromBenchmarksListDestination) {
        screensNavigator.toScreen(destination.screenSpec)
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        fun newInstance(): BenchmarksListFragment {
            return BenchmarksListFragment()
        }
    }
}