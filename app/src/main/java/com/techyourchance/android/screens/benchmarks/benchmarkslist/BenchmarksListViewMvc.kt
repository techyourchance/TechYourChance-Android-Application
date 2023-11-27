package com.techyourchance.android.screens.benchmarks.benchmarkslist

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class BenchmarksListViewMvc(): BaseObservableViewMvc<BenchmarksListViewMvc.Listener>() {

    interface Listener {
        fun onDestinationClicked(destination: FromBenchmarksListDestination)
        fun onBackClicked()
    }

    abstract fun bindDestinations(destinations: List<FromBenchmarksListDestination>)
}