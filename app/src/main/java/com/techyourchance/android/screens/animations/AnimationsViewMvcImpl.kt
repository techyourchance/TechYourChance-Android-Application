package com.techyourchance.android.screens.animations

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.techyourchance.android.R
import com.techyourchance.android.screens.animations.widgets.AnimatedCounter
import com.techyourchance.android.screens.animations.widgets.GradientColoredButton
import com.techyourchance.android.screens.common.toolbar.MyToolbar

class AnimationsViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): AnimationsViewMvc() {

    private val toolbar: MyToolbar
    private val btnGradientColored: GradientColoredButton
    private val btnPlus: Button
    private val btnMinus: Button
    private val animatedCounter: AnimatedCounter

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_animations, parent, false))

        toolbar = findViewById(R.id.toolbar)
        btnGradientColored = findViewById(R.id.btnGradientColored)
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        animatedCounter = findViewById(R.id.animatedCounter)

        btnGradientColored.setText(getString(R.string.animations_gradient_text))

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnGradientColored.setOnClickListener {
            btnGradientColored.toggle()
        }

        btnPlus.setOnClickListener { animatedCounter.increment() }

        btnMinus.setOnClickListener { animatedCounter.decrement() }

    }

}