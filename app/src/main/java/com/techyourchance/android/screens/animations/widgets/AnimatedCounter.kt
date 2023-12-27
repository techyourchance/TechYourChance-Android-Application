package com.techyourchance.android.screens.animations.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.google.android.material.color.MaterialColors
import kotlin.math.abs
import com.google.android.material.R

class AnimatedCounter: View {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 60f
        textAlign = Paint.Align.CENTER
        color = MaterialColors.getColor(context, R.attr.colorOnBackground, Color.BLACK)
    }

    private var topY = 0f
    private var centerY = 0f
    private var bottomY = 0f
    private var translationDist = 0f

    private var textX = 0
    private var count = 0

    private val visibleLabels = mutableListOf<AnimatedLabel>()

    fun increment() {
        val nextCount = count + 1
        if (!visibleLabels.any { it.label == nextCount }) {
            val newLabel = AnimatedLabel(nextCount)
            visibleLabels.add(newLabel)
        }
        count++
        for (label in visibleLabels) {
            label.updateAnimation()
        }
        invalidate()
    }

    fun decrement() {
        val nextCount = count - 1
        if (!visibleLabels.any { it.label == nextCount }) {
            val newLabel = AnimatedLabel(nextCount)
            visibleLabels.add(newLabel)
        }
        count--
        for (label in visibleLabels) {
            label.updateAnimation()
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        textX = width / 2
        topY = this@AnimatedCounter.top.toFloat()
        centerY = (this@AnimatedCounter.top + this@AnimatedCounter.height / 2).toFloat()
        bottomY = (this@AnimatedCounter.top + this@AnimatedCounter.height).toFloat()
        translationDist = abs(topY - centerY)

        visibleLabels.clear()
        visibleLabels.add(AnimatedLabel(count, isSettled = true))
        invalidate()
  }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val iterator = visibleLabels.iterator()
        while (iterator.hasNext()) {
            val visibleLabel = iterator.next()
            if (visibleLabel.animState == AnimatedLabelState.SETTLED && visibleLabel.label != count) {
                iterator.remove()
            } else {
                drawLabel(visibleLabel, canvas)
            }
        }
    }

    private fun drawLabel(label: AnimatedLabel, canvas: Canvas) {
        val alpha = (255 * label.alpha).toInt()
        textPaint.alpha = alpha
        canvas.drawText(label.label.toString(), textX.toFloat(), label.y, textPaint)
    }

    companion object {
        private const val ANIMATION_DURATION_MS = 500L
    }

    private enum class AnimatedLabelState {
        NONE, ENTERING_FROM_TOP, ENTERING_FROM_BOTTOM, SETTLED, EXITING_TO_TOP, EXITING_TO_BOTTOM
    }

    private inner class AnimatedLabel(val label: Int, val isSettled: Boolean = false) {

        private var animator: ValueAnimator? = null

        var y = centerY
        var alpha = 1f
        var animState = if (isSettled) {
            AnimatedLabelState.SETTLED
        } else {
            AnimatedLabelState.NONE
        }

        init {
            updateAnimation()
        }

        fun updateAnimation() {
            var animStartY = centerY
            var animTargetY = centerY
            var animStartAlpha = 1f
            var animTargetAlpha = 1f

            val newAnimState = when(animState) {
                AnimatedLabelState.NONE -> {
                    if (label < count) {
                        animStartY = topY
                        animTargetY = centerY
                        animStartAlpha = 0f
                        animTargetAlpha = 1f
                        AnimatedLabelState.ENTERING_FROM_TOP
                    } else if (label > count) {
                        animStartY = bottomY
                        animTargetY = centerY
                        animStartAlpha = 0f
                        animTargetAlpha = 1f
                        AnimatedLabelState.ENTERING_FROM_BOTTOM
                    } else {
                        AnimatedLabelState.NONE
                    }
                }
                AnimatedLabelState.ENTERING_FROM_TOP -> {
                    if (label < count) {
                        animStartY = y
                        animTargetY = topY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_TOP
                    } else if (label > count) {
                        animStartY = y
                        animTargetY = bottomY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_BOTTOM
                    } else {
                        AnimatedLabelState.ENTERING_FROM_TOP
                    }
                }
                AnimatedLabelState.ENTERING_FROM_BOTTOM -> {
                    if (label < count) {
                        animStartY = y
                        animTargetY = topY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_TOP
                    } else if (label > count) {
                        animStartY = y
                        animTargetY = bottomY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_BOTTOM
                    } else {
                        AnimatedLabelState.ENTERING_FROM_BOTTOM
                    }
                }
                AnimatedLabelState.SETTLED -> {
                    if (label < count) {
                        animStartY = y
                        animTargetY = topY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_TOP
                    } else if (label > count) {
                        animStartY = y
                        animTargetY = bottomY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_BOTTOM
                    } else {
                        AnimatedLabelState.SETTLED
                    }
                }
                AnimatedLabelState.EXITING_TO_TOP -> {
                    if (label < count) {
                        AnimatedLabelState.EXITING_TO_TOP
                    } else if (label > count) {
                        animStartY = y
                        animTargetY = bottomY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_BOTTOM
                    } else {
                        animStartY = y
                        animTargetY = centerY
                        animStartAlpha = alpha
                        animTargetAlpha = 1f
                        AnimatedLabelState.ENTERING_FROM_TOP
                    }
                }
                AnimatedLabelState.EXITING_TO_BOTTOM -> {
                    if (label < count) {
                        animStartY = y
                        animTargetY = topY
                        animStartAlpha = alpha
                        animTargetAlpha = 0f
                        AnimatedLabelState.EXITING_TO_TOP
                    } else if (label > count) {
                        AnimatedLabelState.EXITING_TO_BOTTOM
                    } else {
                        animStartY = y
                        animTargetY = centerY
                        animStartAlpha = alpha
                        animTargetAlpha = 1f
                        AnimatedLabelState.ENTERING_FROM_BOTTOM
                    }
                }
            }

            if (newAnimState == animState) {
                return
            }

            animState = newAnimState

            animator?.cancel()
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = ANIMATION_DURATION_MS
                addUpdateListener {
                    val fraction = animatedValue as Float
                    y = animStartY + fraction * (animTargetY - animStartY)
                    alpha = 1 - abs(y - centerY) / translationDist
                    invalidate()
                }
                doOnCancel {
                    it.removeAllListeners()
                }
                doOnEnd {
                    animState = AnimatedLabelState.SETTLED
                }
                start()
            }
        }
    }
}