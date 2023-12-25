package com.techyourchance.android.screens.composeoverlay

import com.techyourchance.android.screens.common.mvcviews.BaseObservableComposeViewMvc

abstract class ComposeOverlayViewMvc: BaseObservableComposeViewMvc<ComposeOverlayViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleOverlayClicked()
    }

    abstract fun bindIsOverlayShown(isShown: Boolean)
}