package com.techyourchance.android.screens.animations.stackedcards

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.ui.platform.ComposeView
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.mvcviews.ViewMvcType
import com.techyourchance.android.screens.common.toolbar.MyToolbar

class StackedCardsAnimationViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): StackedCardsAnimationViewMvc() {

    private val toolbar: MyToolbar
    private val frameContent: FrameLayout
    private val switchViewsCompose: SwitchCompat

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_stacked_cards_animation, parent, false))

        toolbar = findViewById(R.id.toolbar)
        frameContent = findViewById(R.id.frameContent)
        switchViewsCompose = findViewById(R.id.switchViewsCompose)

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        switchViewsCompose.setOnCheckedChangeListener { _, _ ->
            listeners.map { it.onToggleComposeClicked() }
        }

    }

    override fun setType(viewMvcType: ViewMvcType) {
        frameContent.removeAllViews()
        when(viewMvcType) {
            ViewMvcType.VIEW_BASED -> {
                switchViewsCompose.isChecked = false
                frameContent.addView(StackedCardsView(context))
            }
            ViewMvcType.COMPOSE_BASED -> {
                switchViewsCompose.isChecked = true
                frameContent.addView(ComposeView(context).apply {
                    setContent {
                        StackedCardsCompose()
                    }
                })
            }
        }
    }

}