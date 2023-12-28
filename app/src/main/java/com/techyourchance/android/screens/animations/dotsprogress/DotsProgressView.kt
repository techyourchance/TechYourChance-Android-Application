package com.techyourchance.android.screens.animations.dotsprogress

import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.android.material.color.MaterialColors
import com.google.android.material.R

class DotsProgressView: View {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = MaterialColors.getColor(rootView, R.attr.colorPrimary)
    }

    private var dotRadius = 0f
    private var dotSize = 0f
    private var dotSpacing = 0f
    private var centerY = 0f
    private var offsetX = 0f

    private var dotOffsetY1: Float = 0f
    private var dotOffsetY2: Float = 0f
    private var dotOffsetY3: Float = 0f

    private var animatorSet: AnimatorSet? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(
            offsetX + dotRadius,
            centerY + dotSize * dotOffsetY1,
            dotRadius,
            paint
        )
        canvas.drawCircle(
            offsetX + dotSize + dotSpacing + dotRadius,
            centerY + dotSize * dotOffsetY2,
            dotRadius,
            paint
        )
        canvas.drawCircle(
            offsetX + 2 * (dotSize + dotSpacing) + dotRadius,
            centerY + dotSize * dotOffsetY3,
            dotRadius,
            paint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        dotSize = 50f
        dotRadius = dotSize / 2f
        dotSpacing = dotRadius
        offsetX = (w - 3 * dotSize - 2 * dotSpacing) / 2
        centerY = h / 2f
        dotOffsetY1 = centerY
        dotOffsetY2 = centerY
        dotOffsetY3 = centerY
        startAnimation()
    }


    private fun startAnimation() {

        val animator1 = getAnimator(
            listOf(
                Keyframe.ofFloat(0f, 0.5f),
                Keyframe.ofFloat(0.2f, 0f),
                Keyframe.ofFloat(0.4f, 1f),
                Keyframe.ofFloat(0.6f, 0.5f),
                Keyframe.ofFloat(0.8f, 0.5f),
                Keyframe.ofFloat(1f, 0.5f),
            ),
            ValueAnimator.AnimatorUpdateListener {
                dotOffsetY1 = it.animatedValue as Float
                invalidate()
            }
        )

        val animator2 = getAnimator(
            listOf(
                Keyframe.ofFloat(0f, 0.5f),
                Keyframe.ofFloat(0.2f, 0.5f),
                Keyframe.ofFloat(0.4f, 0f),
                Keyframe.ofFloat(0.6f, 1f),
                Keyframe.ofFloat(0.8f, 0.5f),
                Keyframe.ofFloat(1f, 0.5f),
            ),
            ValueAnimator.AnimatorUpdateListener {
                dotOffsetY2 = it.animatedValue as Float
                invalidate()
            }
        )

        val animator3 = getAnimator(
            listOf(
                Keyframe.ofFloat(0f, 0.5f),
                Keyframe.ofFloat(0.2f, 0.5f),
                Keyframe.ofFloat(0.4f, 0.5f),
                Keyframe.ofFloat(0.6f, 0f),
                Keyframe.ofFloat(0.8f, 1f),
                Keyframe.ofFloat(1f, 0.5f),
            ),
            ValueAnimator.AnimatorUpdateListener {
                dotOffsetY3 = it.animatedValue as Float
                invalidate()
            }
        )

        animatorSet?.cancel()

        animatorSet = AnimatorSet().apply {
            playTogether(animator1, animator2, animator3)
            start()
        }
    }

    private fun getAnimator(
        keyframes: List<Keyframe>,
        updateListener: ValueAnimator.AnimatorUpdateListener
    ): ValueAnimator {
        val propertyValuesHolder = PropertyValuesHolder.ofKeyframe(
            "dummy", *keyframes.toTypedArray()
        )

        return ObjectAnimator.ofPropertyValuesHolder(propertyValuesHolder).apply {
            duration = ANIMATION_DURATION_MS
            addUpdateListener(updateListener)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
    }

    companion object {
        const val ANIMATION_DURATION_MS = 1000L
    }

}