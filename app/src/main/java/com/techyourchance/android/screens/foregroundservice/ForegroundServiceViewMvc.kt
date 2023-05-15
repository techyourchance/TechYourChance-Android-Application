package com.techyourchance.android.screens.foregroundservice

import com.techyourchance.android.backgroundwork.ForegroundServiceState
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class ForegroundServiceViewMvc(): BaseObservableViewMvc<ForegroundServiceViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleServiceClicked()
    }

    abstract fun bindServiceState(state: ForegroundServiceState)
}