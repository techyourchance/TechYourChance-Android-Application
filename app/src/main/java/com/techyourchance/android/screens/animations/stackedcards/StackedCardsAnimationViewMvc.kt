package com.techyourchance.android.screens.animations.stackedcards

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc
import com.techyourchance.android.screens.common.mvcviews.ViewMvcType

abstract class StackedCardsAnimationViewMvc(): BaseObservableViewMvc<StackedCardsAnimationViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onToggleComposeClicked()
    }

    abstract fun setType(viewMvcType: ViewMvcType)
}