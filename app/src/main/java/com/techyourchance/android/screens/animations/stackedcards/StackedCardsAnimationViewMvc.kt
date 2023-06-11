package com.techyourchance.android.screens.animations.stackedcards

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class StackedCardsAnimationViewMvc(): BaseObservableViewMvc<StackedCardsAnimationViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
    }
}