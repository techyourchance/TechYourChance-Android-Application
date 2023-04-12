package com.techyourchance.template.screens.home

import com.techyourchance.template.screens.common.mvcviews.BaseObservableViewMvc

abstract class HomeViewMvc(): BaseObservableViewMvc<HomeViewMvc.Listener>() {

    interface Listener {
        fun onDestinationClicked(destination: FromHomeDestination)
    }

    abstract fun bindDestinations(destinations: List<FromHomeDestination>)
}