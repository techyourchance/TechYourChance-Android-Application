package com.techyourchance.android.screens.ndkbasics

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class NdkBasicsViewMvc(): BaseObservableViewMvc<NdkBasicsViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onComputeFibonacciClicked()
    }

    abstract fun getArgument(): Int

}