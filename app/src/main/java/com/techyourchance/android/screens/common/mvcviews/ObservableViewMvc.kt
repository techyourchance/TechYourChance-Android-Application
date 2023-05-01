package com.techyourchance.android.screens.common.mvcviews

interface ObservableViewMvc<LISTENER_CLASS> : ViewMvc {
    fun registerListener(listener: LISTENER_CLASS)
    fun unregisterListener(listener: LISTENER_CLASS)
}