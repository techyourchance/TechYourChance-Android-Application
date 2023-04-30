package com.techyourchance.template.screens.ndkbasics

import com.techyourchance.template.screens.common.mvcviews.BaseObservableViewMvc

abstract class NdkBasicsViewMvc(): BaseObservableViewMvc<NdkBasicsViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onComputeFibonacciClicked()
    }

    abstract fun getArgument(): Int

}