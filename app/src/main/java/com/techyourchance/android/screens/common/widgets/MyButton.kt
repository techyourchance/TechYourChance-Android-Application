package com.techyourchance.android.screens.common.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MyButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var lastPerformedClickMillis = 0L

    override fun setEnabled(enabled: Boolean) {
        alpha = if (enabled) 1f else ALPHA_DISABLED
        super.setEnabled(enabled)
    }

    override fun performClick(): Boolean {
        return if (System.currentTimeMillis() > lastPerformedClickMillis + DEBOUNCE_TIMEOUT_MS) {
            val clickResult = super.performClick()
            lastPerformedClickMillis = System.currentTimeMillis()
            clickResult
        } else {
            false
        }
    }

    companion object {
        private const val DEBOUNCE_TIMEOUT_MS = 200L
        private const val ALPHA_DISABLED = 0.6f
    }
}