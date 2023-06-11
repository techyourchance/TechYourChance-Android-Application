package com.techyourchance.android.screens.animations.stackedcards

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.techyourchance.android.R
import com.techyourchance.android.screens.animations.widgets.AnimatedCounter
import com.techyourchance.android.screens.animations.widgets.GradientColoredButton
import com.techyourchance.android.screens.common.toolbar.MyToolbar

class StackedCardsAnimationViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): StackedCardsAnimationViewMvc() {

    private val toolbar: MyToolbar

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_stacked_cards_animation, parent, false))

        toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }
    }

}