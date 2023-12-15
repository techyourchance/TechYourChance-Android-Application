package com.techyourchance.android.screens.animations.dotsprogress

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc
import com.techyourchance.android.screens.common.mvcviews.ViewMvcType

abstract class DotsProgressAnimationViewMvc(): BaseObservableViewMvc<DotsProgressAnimationViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleComposeClicked()
    }

    abstract fun setType(viewMvcType: ViewMvcType)
}