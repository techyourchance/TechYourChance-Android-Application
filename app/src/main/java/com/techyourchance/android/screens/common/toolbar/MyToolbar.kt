package com.techyourchance.android.screens.common.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.techyourchance.android.R

class MyToolbar : FrameLayout {

    interface NavigateUpListener {
        fun onNavigationUpClicked()
    }

    private var navigateUpListener: () -> Unit = {}

    private lateinit var viewNavigateUp: View
    private lateinit var txtTitle: TextView


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
        val view = LayoutInflater.from(context).inflate(R.layout.layout_my_toolbar, this, true).apply {
            viewNavigateUp = findViewById(R.id.navigate_up)
            txtTitle = findViewById(R.id.txtToolbarTitle)
        }

        viewNavigateUp.setOnClickListener { navigateUpListener.invoke() }
    }

    fun setNavigateUpListener(navigateUpListener: () -> Unit) {
        this.navigateUpListener = navigateUpListener
        viewNavigateUp.isVisible = true
    }

    fun setTitle(title: String) {
        txtTitle.text = title
        txtTitle.isVisible = true
    }
}