package com.techyourchance.android.screens.userinterfaces

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class UserInterfacesViewMvc(): BaseObservableViewMvc<UserInterfacesViewMvc.Listener>() {

    interface Listener {
        fun onDestinationClicked(destination: FromUserInterfacesDestination)
        fun onBackClicked()
    }

    abstract fun bindDestinations(destinations: List<FromUserInterfacesDestination>)
}