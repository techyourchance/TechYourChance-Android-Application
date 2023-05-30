package com.techyourchance.android.screens.animations

import android.view.LayoutInflater
import android.view.ViewGroup
import com.techyourchance.android.R
import com.techyourchance.android.screens.animations.widgets.GradientColoredButton
import com.techyourchance.android.screens.common.toolbar.MyToolbar

class AnimationsViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): AnimationsViewMvc() {

    private val toolbar: MyToolbar
    private val btnGradientColored: GradientColoredButton

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_animations, parent, false))

        toolbar = findViewById(R.id.toolbar)
        btnGradientColored = findViewById(R.id.btnGradientColored)

        btnGradientColored.setText("Gradient text")

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnGradientColored.setOnClickListener {
            btnGradientColored.toggle()
        }

    }

}