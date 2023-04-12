package com.techyourchance.template.screens.common.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.techyourchance.template.R

class MyToolbar : Toolbar {

    interface NavigateUpListener {
        fun onNavigationUpClicked()
    }

    private var navigateUpListener: () -> Unit = {}

    private lateinit var navigateUp: FrameLayout

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_my_toolbar, this, true)
        setContentInsetsRelative(0, 0)
        navigateUp = view.findViewById(R.id.navigate_up)
        navigateUp.setOnClickListener { navigateUpListener.invoke() }
    }

    fun setNavigateUpListener(navigateUpListener: () -> Unit) {
        this.navigateUpListener = navigateUpListener
        navigateUp.visibility = View.VISIBLE
    }
}