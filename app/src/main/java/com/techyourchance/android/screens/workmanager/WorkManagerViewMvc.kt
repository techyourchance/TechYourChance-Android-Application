package com.techyourchance.android.screens.workmanager

import com.techyourchance.android.backgroundwork.workmanager.MyWorkerState
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class WorkManagerViewMvc(): BaseObservableViewMvc<WorkManagerViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleWorkClicked()
    }

    abstract fun getIsExpedited(): Boolean
    abstract fun getIsNetworkConstrained(): Boolean
    abstract fun bindWorkerState(state: MyWorkerState)
}