package com.techyourchance.android.screens.home

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class HomeViewMvc(): BaseObservableViewMvc<HomeViewMvc.Listener>() {

    interface Listener {
        fun onDestinationClicked(destination: FromHomeDestination)
    }

    abstract fun bindDestinations(destinations: List<FromHomeDestination>)
}