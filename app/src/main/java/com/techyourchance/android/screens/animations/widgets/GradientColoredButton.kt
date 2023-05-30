package com.techyourchance.android.screens.animations.widgets

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.techyourchance.android.R

class GradientColoredButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderBounds = RectF()
    private var textX: Int = 0
    private var textY: Int = 0

    private var gradient1Start = ContextCompat.getColor(context, R.color.blue)
    private var gradient1End = ContextCompat.getColor(context, R.color.blue_dark)
    private var gradient2Start = ContextCompat.getColor(context, R.color.orange)
    private var gradient2End = ContextCompat.getColor(context, R.color.pink)

    private var myToggleAnimator: MyToggleAnimator? = null

    private val cornerRadius = 30f
    private val colorAnimationDurationMs = 250L
    private var text = ""

    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    fun toggle() {
        if (myToggleAnimator == null) {
            myToggleAnimator = MyToggleAnimator()
            myToggleAnimator!!.animateToggle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val borderOffset = borderPaint.strokeWidth / 2
        borderBounds.set(borderOffset, borderOffset, width - borderOffset, height - borderOffset)
        textX = width / 2
        textY = (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()
        updateGradients()
  }

    private fun updateGradients() {
        textPaint.shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            intArrayOf(gradient2Start, gradient2End),
            null,
            Shader.TileMode.CLAMP
        )
        borderPaint.shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            intArrayOf(gradient2Start, gradient2End),
            null,
            Shader.TileMode.CLAMP
        )
        backgroundPaint.shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            intArrayOf(gradient1Start, gradient1End),
            null,
            Shader.TileMode.CLAMP
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the background
        canvas.drawRoundRect(borderBounds, cornerRadius, cornerRadius, backgroundPaint)
        // Draw the border
        canvas.drawRoundRect(borderBounds, cornerRadius, cornerRadius, borderPaint)
        // Draw the text centered
        canvas.drawText(text, textX.toFloat(), textY.toFloat(), textPaint)
    }

    private inner class MyToggleAnimator {
        private val evaluator = ArgbEvaluator()
        private val gradient1StartFreeze = gradient1Start
        private val gradient1EndFreeze = gradient1End
        private val gradient2StartFreeze = gradient2Start
        private val gradient2EndFreeze = gradient2End

        fun animateToggle() {
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = colorAnimationDurationMs
                addUpdateListener {
                    val fraction = animatedValue as Float
                    gradient1Start = evaluator.evaluate(fraction, gradient1StartFreeze, gradient2StartFreeze) as Int
                    gradient1End = evaluator.evaluate(fraction, gradient1EndFreeze, gradient2EndFreeze) as Int
                    gradient2Start = evaluator.evaluate(fraction, gradient2StartFreeze, gradient1StartFreeze) as Int
                    gradient2End = evaluator.evaluate(fraction, gradient2EndFreeze, gradient1EndFreeze) as Int
                    updateGradients()
                }
                doOnEnd {
                    myToggleAnimator = null
                }
                start()
            }
        }
    }
}