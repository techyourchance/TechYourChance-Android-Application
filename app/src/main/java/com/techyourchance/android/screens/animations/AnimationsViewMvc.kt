package com.techyourchance.android.screens.animations

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class AnimationsViewMvc(): BaseObservableViewMvc<AnimationsViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
    }
}