package com.techyourchance.android.screens.benchmarks

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class BenchmarksViewMvc(): BaseObservableViewMvc<BenchmarksViewMvc.Listener>() {

    interface Listener {
        fun onDestinationClicked(destination: FromBenchmarksDestination)
        fun onBackClicked()
    }

    abstract fun bindDestinations(destinations: List<FromBenchmarksDestination>)
}